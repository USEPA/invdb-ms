package gov.epa.ghg.invdb.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import gov.epa.ghg.invdb.model.ArchiveAttachment;
import gov.epa.ghg.invdb.repository.ArchiveAttachmentRepository;
import gov.epa.ghg.invdb.repository.QcAnalyticsViewerRepository;
import gov.epa.ghg.invdb.rest.dto.QcAnalyticsFormDto;
import gov.epa.ghg.invdb.util.FileUtil;
import gov.epa.ghg.invdb.util.JsonUtil;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
@Log4j2
public class QcAnalyticsService {
    @Autowired
    private S3Service s3Service;
    @Autowired
    private JsonUtil jsonUtil;
    @Value("${s3-directory}")
    private String s3FilePath;
    @Value("${s3.bucket}")
    private String bucket;
    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private ArchiveAttachmentRepository archiveAttchRepository;
    @Autowired
    private QcAnalyticsViewerRepository analyticsViewerRepository;
    @Value("${archive-directory}")
    private String archiveFilePath;

    public void downloadFile(String key, String filename) throws IOException, Exception {
        String filepath = s3FilePath + filename;
        boolean isFileExists = fileUtil.checkIfFileExists(filepath);
        if (!isFileExists) {
            try (ResponseInputStream<GetObjectResponse> responseStream = s3Service.getS3Object(bucket, key)) {
                fileUtil.writeInputStreamToFile(responseStream, filepath);
            } catch (Exception e) {
                log.error("An error occured getting s3 object: ", e);
                throw e;
            }
        }
    }

    public List<String> getSpecifications(String folder) throws Exception {
        String key = "analytics/qc/" + folder + "/metadata.json";
        byte[] jsonData = s3Service.downloadFile(bucket, key);
        // Convert byte[] to String
        String jsonString = new String(jsonData, StandardCharsets.UTF_8);

        // Parse the String as JSON
        JSONObject jsonObject = new JSONObject(jsonString);
        // Access the recalcs object
        JSONObject recalcsObject = jsonObject.getJSONObject("recalcs");
        JSONArray outputYearsJsonArray = recalcsObject.getJSONArray("outputYears");
        List<String> outputYears = new ArrayList<>();
        for (int i = 0; i < outputYearsJsonArray.length(); i++) {
            outputYears.add(outputYearsJsonArray.getString(i));
        }
        return outputYears;
    }

    public ArrayNode extractDataNodes(String filename, String searchKey) throws Exception {
        return jsonUtil.parseJsonForKeyData(s3FilePath + filename, searchKey);
    }

    public JsonNode extractRawData(String filename, String searchKey) throws Exception {
        return jsonUtil.parseJsonRawData(s3FilePath + filename, searchKey);
    }

    public void processPkgAttchAndExtractJson(String filename, Long archiveAttachmentId) throws Exception {
        // check if file is already present in local
        boolean isFileExists = fileUtil.checkIfFileExists(archiveFilePath + filename);
        if (isFileExists) {
            log.info(filename + " already exists in local directory " + archiveFilePath);
            System.out.println(filename + " already exists in local directory " + archiveFilePath);
        }
        if (!isFileExists) {
            String jsonFileName = filename.split("-", 2)[1];
            ArchiveAttachment attachment = archiveAttchRepository.findById(archiveAttachmentId).orElse(null);
            if (attachment == null || attachment.getAttachmentContent() == null) {
                throw new Exception("No records found in archive_attachment table for id:" + archiveAttachmentId);
            }
            try (InputStream inputStream = new ByteArrayInputStream(attachment.getAttachmentContent());
                    ZipInputStream zis = new ZipInputStream(inputStream)) {
                ZipEntry zipEntry;

                // Loop through the entries and look for the JSON file
                while ((zipEntry = zis.getNextEntry()) != null) {
                    if (zipEntry.getName().equals(jsonFileName + ".json") || zipEntry.getName().equals(jsonFileName)) {
                        // Write the JSON content to a file
                        fileUtil.writeInputStreamToFile(zis, archiveFilePath + filename);
                        log.info("JSON file '" + filename + "' extracted and written to " + archiveFilePath);
                        System.out.println("JSON file '" + filename + "' extracted and written to " + archiveFilePath);
                        break;
                    }
                }
            }
        }
    }

    public void processCurrentAndExtractJson(String filename, Integer yearId, Integer layerId, Integer userId)
            throws Exception {
        // check if file is already present in local
        boolean isFileExists = fileUtil.checkIfFileExists(archiveFilePath + filename);
        if (isFileExists) {
            log.info(filename + " already exists in local directory " + archiveFilePath);
            System.out.println(filename + " already exists in local directory " + archiveFilePath);
        }
        if (!isFileExists) {
            System.out.println("Get current dataset for yearId: " + yearId + ", layerId: " + layerId);
            String jsonString = analyticsViewerRepository.getCurrentDatasetJson(yearId, layerId, userId);
            try (InputStream inputStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8))) {
                fileUtil.writeInputStreamToFile(inputStream, archiveFilePath + filename);
                log.info("JSON for current extracted and written to " + archiveFilePath);
                System.out.println("JSON for current extracted and written to " + archiveFilePath);
            }
        }
    }

    public Map<String, List<Map<String, String>>> extractSecSubsecCatFromJson(String filename) throws Exception {
        /*
         * {
         * sector : [{id: "0", name: ""}, {id: "1", name: ""}],
         * subsector: [{id: "0-0", name: ""}, {id: "0-1", name: ""}],
         * category: [{id: "0-0-0", name: ""}, {id: "0-0-1", name: ""}, {id: "0-1-1",
         * name: ""}]
         * }
         */
        // Our resulting data structure
        Map<String, List<Map<String, String>>> resultMap = new HashMap<>();
        resultMap.put("sector", new ArrayList<>());
        resultMap.put("subsector", new ArrayList<>());
        resultMap.put("category", new ArrayList<>());
        // Maps to keep track of indices
        Map<String, Integer> sectorIndices = new HashMap<>();
        Map<String, String> subsectorIndices = new HashMap<>();
        Map<String, String> categoryIndices = new HashMap<>();
        JsonFactory jsonFactory = new JsonFactory();
        try (JsonParser jsonParser = jsonFactory.createParser(new File(archiveFilePath + filename))) {
            String sector;
            String subsector;
            String category;

            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                    // Reset the values for a new object
                    sector = null;
                    subsector = null;
                    category = null;
                    // Iterate over the fields of each object
                    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                        String fieldName = jsonParser.getCurrentName();
                        // Move to the value token
                        jsonParser.nextToken();
                        String value = jsonParser.getText();
                        switch (fieldName) {
                            case "sector":
                                sector = value;
                                break;
                            case "subsector":
                                subsector = value;
                                break;
                            case "category":
                                category = value;
                                break;
                        }
                    }
                    if (sector != null && subsector != null && category != null) {
                        Integer sectorId = sectorIndices.computeIfAbsent(sector, k -> {
                            resultMap.get("sector")
                                    .add(Map.of("id", String.valueOf(sectorIndices.size()), "name", k));
                            return sectorIndices.size();
                        });
                        String fSubsector = subsector;
                        String subsectorId = subsectorIndices.computeIfAbsent(sectorId + "-" + subsector, k -> {
                            resultMap.get("subsector")
                                    .add(Map.of("id", sectorId + "-" + subsectorIndices.size(), "name", fSubsector));
                            return sectorId + "-" + subsectorIndices.size();
                        });
                        String fCategory = category;
                        categoryIndices.computeIfAbsent(subsectorId + "-" + category, k -> {
                            resultMap.get("category")
                                    .add(Map.of("id", subsectorId + "-" + categoryIndices.size(), "name", fCategory));
                            return subsectorId + "-" + subsectorIndices.size();
                        });
                    }
                }
            }
        }
        return resultMap;
    }

    public void processAndWriteToFile(String filename, List<String> selectedCategories)
            throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(jsonFactory);
        File sourceFile = new File(archiveFilePath + filename);
        File destinationFile = new File(archiveFilePath + "/filtered/" + filename);
        try (
                JsonParser jsonParser = jsonFactory.createParser(sourceFile);
                JsonGenerator jsonGenerator = jsonFactory.createGenerator(new FileOutputStream(destinationFile),
                        JsonEncoding.UTF8)) {
            jsonGenerator.writeStartArray(); // Begin the array for the destination file

            // Skip to the start of the array '[' in the source file
            if (jsonParser.nextToken() == JsonToken.START_ARRAY) {
                while (jsonParser.nextToken() == JsonToken.START_OBJECT) {
                    Map<String, Object> dataMap = new HashMap<>();
                    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                        String fieldName = jsonParser.getCurrentName();
                        jsonParser.nextToken();
                        dataMap.put(fieldName, mapper.readValue(jsonParser, Object.class)); // Read value into map
                    }
                    if (selectedCategories.contains(dataMap.get("category"))) {
                        jsonGenerator.writeObject(dataMap); // Only write if category is in selectedCategories
                    }
                }
            }
            jsonGenerator.writeEndArray();
        }
    }

    public void uploadFileToS3(String srcFilename, String destFilename) throws Exception {
        File file = new File(archiveFilePath + srcFilename);
        if (!file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("The provided filename does not exist or is a directory");
        }
        String key = "analytics/qc/" + destFilename;
        s3Service.uploadFile(file, bucket, key);
    }

    public void uploadBytesToS3(byte[] bytes, String destFilename) throws Exception {
        String key = "analytics/qc/" + destFilename;
        s3Service.uploadFile(bytes, bucket, key);
    }

    public String generateSpecificationsJson(QcAnalyticsFormDto dto) throws IOException {
        // Convert to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        // Create the JSON structure using Maps
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("baselineYearLayerKey", dto.getBaselineYearLayerKey());
        jsonMap.put("baselineObjName",
                dto.getBaselineObjName().startsWith("0-") ? "Current" : dto.getBaselineObjName());
        jsonMap.put("isRecalculations", dto.getIsRecalculations());
        // Recalculations structure
        if (dto.getIsRecalculations()) {
            Map<String, Object> recalculations = new HashMap<>();
            jsonMap.put("comparatorYearLayerKey", dto.getComparatorYearLayerKey());
            jsonMap.put("comparatorObjName",
                    dto.getComparatorObjName().startsWith("0-") ? "Current" : dto.getComparatorObjName());
            recalculations.put("parameter", dto.getRecalcParameter());
            recalculations.put("threshold", dto.getRecalcThreshold());
            recalculations.put("outputYears", dto.getRecalcYears());
            jsonMap.put("recalculations", recalculations);
        }
        jsonMap.put("isOutliers", dto.getIsTimeseriesOutlier());
        // Outliers structure
        if (dto.getIsTimeseriesOutlier()) {
            Map<String, Object> outliers = new HashMap<>();
            outliers.put("tsYearsSelected", dto.getTsYearsSelected());
            jsonMap.put("outliers", outliers);
        }

        jsonMap.put("categories", dto.getCategories());
        jsonMap.put("columns", dto.getColumns());
        jsonMap.put("ghgOption", dto.getGhgOption());

        // Convert the map to a JSON string
        return objectMapper.writeValueAsString(jsonMap);
        // return jsonString.getBytes();
    }

    public String getTimestampedFolderName() {
        // Get current date and time
        LocalDateTime now = LocalDateTime.now();
        // Define a date time formatter pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // Format the current date and time
        String folderName = now.format(formatter);
        // Return the folder name
        return folderName;
    }

    /** only for testing purpose delete late */
    public String verifyS3Files(String filename) throws IOException, Exception {
        String key = "analytics/qc/20250107" + filename;
        System.out.println("BUCKET: " + bucket + " KEY: " + key);
        try (ResponseInputStream<GetObjectResponse> responseStream = s3Service.getS3Object(bucket,
                key)) {
            fileUtil.writeInputStreamToFile(responseStream, s3FilePath + "/verify/" + filename);
        } catch (Exception e) {
            log.error("An error occured getting s3 object: ", e);
            throw e;
        }
        return "success";
    }
}
