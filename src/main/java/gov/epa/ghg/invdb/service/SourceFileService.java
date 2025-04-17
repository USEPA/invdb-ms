package gov.epa.ghg.invdb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.epa.ghg.invdb.enumeration.ValidationStatus;
import gov.epa.ghg.invdb.model.SourceFile;
import gov.epa.ghg.invdb.model.SourceFileAttachment;
import gov.epa.ghg.invdb.repository.DimSourceNameRepository;
import gov.epa.ghg.invdb.model.DimPublicationYear;
import gov.epa.ghg.invdb.repository.DimPublicationYearRepository;
import gov.epa.ghg.invdb.repository.SourceFileAttachmentRepository;
import gov.epa.ghg.invdb.repository.SourceFileRepository;
import gov.epa.ghg.invdb.rest.dto.DimPublicationYearDto;
import gov.epa.ghg.invdb.rest.dto.DimSourceNameDto;
import gov.epa.ghg.invdb.rest.dto.SourceFileDto;
import gov.epa.ghg.invdb.util.ExcelUtil;
import gov.epa.ghg.invdb.util.FileUtil;
import jakarta.transaction.Transactional;

@Service
public class SourceFileService {
    @Autowired
    private SourceFileRepository sourceFileRepository;

    @Autowired
    private SourceFileAttachmentRepository attachmentRepository;

    @Autowired
    private DimSourceNameRepository dimSourceNameRepository;

    @Autowired
    private DimPublicationYearRepository dimPubYearRepository;

    @Autowired
    private ExcelUtil excelUtil;
    @Autowired
    private FileUtil fileUtil;

    @Transactional
    public void save(List<MultipartFile> files, List<SourceFileDto> existingSrcFiles,
            List<DimSourceNameDto> sourceNames, int layerId, int reportingYr, int userId)
            throws Exception {
        int template = (reportingYr >= 2025) ? 3 : 2;
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(fileName);
            boolean isCsv = extension.equals("csv");
            boolean isJson = extension.equals("json");
            SourceFile sourceFile = null;
            SourceFileAttachment attachment = null;
            Date today = new Date();
            byte[] fileContent = fileUtil.getFilebytes(file);
            String sourceName = "";

            if (template == 2) {
                sourceName = excelUtil.readCell(file, "InvDB", 8, 3).orElse(""); // c8
            }

            int sourceNameId = -1;

            List<DimPublicationYear> pubYears = dimPubYearRepository.findAll();
            DimPublicationYear pubYear = pubYears.stream().filter(y -> y.getYear() == reportingYr).findFirst()
                    .orElse(null);

            // get source name from combo if template 3
            if (template == 3) {
                String sector, subsector, category, subcategory1;
                sector = subsector = category = subcategory1 = "";
                if (isCsv) {
                    // csv
                    sector = excelUtil.readCsvCell(file, 2, 2).orElse("").trim(); // b2
                    subsector = excelUtil.readCsvCell(file, 2, 3).orElse("").trim(); // c2
                    category = excelUtil.readCsvCell(file, 2, 4).orElse("").trim(); // d2
                    subcategory1 = excelUtil.readCsvCell(file, 2, 5).orElse("").trim(); // e2
                } else if (isJson) {
                    // json
                    String jsonString = new String(file.getBytes());

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rowsArray = objectMapper.readTree(jsonString);

                    if (rowsArray != null && rowsArray.isArray() && rowsArray.size() > 0) {
                        JsonNode firstDataRow = rowsArray.get(0);
                        sector = firstDataRow.get("Sector").asText();
                        subsector = firstDataRow.get("Subsector").asText();
                        category = firstDataRow.get("Category").asText();
                        subcategory1 = firstDataRow.get("Subcategory1").asText();
                    }
                } else {
                    // expect excel
                    sector = excelUtil.readCell(file, "InvDB", 2, 2).orElse("").trim(); // b2
                    subsector = excelUtil.readCell(file, "InvDB", 2, 3).orElse("").trim(); // c2
                    category = excelUtil.readCell(file, "InvDB", 2, 4).orElse("").trim(); // d2
                    subcategory1 = excelUtil.readCell(file, "InvDB", 2, 5).orElse("").trim(); // e2
                }

                List<DimSourceNameDto> possibleSourceNames = dimSourceNameRepository.checkTemplate3DimSourceName(sector,
                        subsector, category, subcategory1, layerId, reportingYr);
                if (possibleSourceNames.size() > 0) {
                    sourceNameId = possibleSourceNames.get(0).getId();
                    sourceName = possibleSourceNames.get(0).getName();
                }
            }
            // Check if there is already an existing file for this source
            String finalSourceName = sourceName;
            DimSourceNameDto sourceNameObj = sourceNames.stream()
                    .filter(s -> s.getName().equalsIgnoreCase(finalSourceName) && s.getPubYearId() == pubYear.getId())
                    .findFirst().orElse(null);
            if (sourceNameObj == null) {
                throw new Exception("Unable to find a source name for template " + template + " file: " + fileName);
            }
            SourceFileDto existingSrcFile = existingSrcFiles.stream()
                    .filter(f -> f.getSourceNameId().equals(sourceNameObj.getId())).findFirst().orElse(null);
            if (existingSrcFile != null) {
                sourceFile = sourceFileRepository.findById(existingSrcFile.getSourceFileId()).orElse(null);
            }
            if (sourceFile == null) {
                sourceFile = new SourceFile();
                sourceFile.setSourceNameId(sourceNameId > 0 ? sourceNameId : sourceNameObj.getId());
                sourceFile.setReportingYear(reportingYr);
                sourceFile.setLayerId(layerId);
                sourceFile.setCreatedDate(today);
                sourceFile.setCreatedBy(userId);
            }
            sourceFile.setLastUpdatedBy(userId);
            sourceFile.setLastUpdatedDate(today);
            sourceFile.setValidationStatus(null);
            sourceFile.setLastAttchLinkedDt(today);
            sourceFile.setDeleted(false);
            sourceFileRepository.save(sourceFile);
            if (sourceFile.getSourceFileId() != null) {
                attachment = new SourceFileAttachment();
                attachment.setAttachmentName(fileName);
                attachment.setAttachmentType(file.getContentType());
                attachment.setAttachmentSize(file.getSize());
                attachment.setContent(fileContent);
                attachment.setSourceFileId(sourceFile.getSourceFileId());
                attachment.setLastSrcfileLinkedDt(today);
                attachment.setCreatedBy(userId);
                attachment.setCreatedDate(today);
                attachment.setLastUpdatedBy(userId);
                attachment.setLastUpdatedDate(today);
                attachment.setHasError(false);
                attachmentRepository.save(attachment);
            } else {
                throw new Exception("Something went wrong....sourceFileId is null");
            }
        }
    }

    @Transactional
    public Date revert(Long attachmentId, int userId) throws Exception {
        try {
            SourceFileAttachment attachment = attachmentRepository.findById(attachmentId).orElse(null);
            if (attachment == null) {
                throw new Exception("Something went wrong....Attachment is missing for attachmentId " + attachmentId);
            }
            SourceFile sourceFile = sourceFileRepository.findById(attachment.getSourceFileId()).orElse(null);
            if (sourceFile == null) {
                throw new Exception(
                        "Something went wrong....Source file is missing for sourceId " + attachment.getSourceFile());
            }
            // update link dates on source file and attachment
            Date today = new Date();
            attachment.setLastSrcfileLinkedDt(today);
            attachment.setProcessedDate(null);
            attachment.setLastUpdatedBy(userId);
            attachment.setLastUpdatedDate(today);

            sourceFile.setLastAttchLinkedDt(today);
            sourceFile.setValidationStatus(ValidationStatus.SUCCESS.getValue());
            sourceFile.setLastUpdatedBy(userId);
            sourceFile.setLastUpdatedDate(today);
            ;
            return today;
        } catch (Exception e) {
            throw e;
        }
    }

}
