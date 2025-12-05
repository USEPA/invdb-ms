package gov.epa.ghg.invdb.rest.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import gov.epa.ghg.invdb.model.QcAnalyticsViewer;
import gov.epa.ghg.invdb.repository.QcAnalyticsViewerRepository;
import gov.epa.ghg.invdb.rest.dto.QcAnalyticsViewerDto;
import gov.epa.ghg.invdb.rest.helper.QcAnalyticsHelper;
import gov.epa.ghg.invdb.service.QcAnalyticsService;
import gov.epa.ghg.invdb.service.RestService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/qualityAnalytics/viewer")
@Log4j2
public class QcAnalyticsViewerController {
    @Autowired
    private QcAnalyticsService qcService;
    @Autowired
    private QcAnalyticsViewerRepository analyticsViewerRepository;
    @Autowired
    private QcAnalyticsHelper qcAnalyticsHelper;
    @Autowired
    private RestService restService;

    // @GetMapping("/getQcData")
    // public void getQcData(HttpServletResponse response) throws Exception {
    // byte[] reportData = s3Service.downloadFile("invdb-test-data-files",
    // "qc-data/qcData.json");
    // attachmentHelper.createFileDownloadResponse(response, reportData, "abc",
    // "json");
    // }

    // @GetMapping("/queryS3Bucket")
    // public void getQueryResults(@RequestParam(name = "query") String query)
    // throws Exception {
    // ArrayList<Map<String, String>> results = athenaService
    // .runQuery(query);
    // System.out.println("results");
    // }

    @GetMapping("/load")
    public Map<String, Object> getViewerRecords() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        // List<Long> runningJobs = new ArrayList<>();
        boolean isJobRunning = false;
        List<QcAnalyticsViewerDto> viewerDtos = new ArrayList<>();
        List<QcAnalyticsViewer> viewerRecords = analyticsViewerRepository.findAll();
        // Read specifications to get metadata
        for (QcAnalyticsViewer record : viewerRecords) {
            // Parse the String as JSON
            JSONObject jsonObject = new JSONObject(record.getSpecifications());

            // Access values using keys
            String baselineYrLayer = jsonObject.optString("baselineYearLayerKey");
            String baselineObject = jsonObject.optString("baselineObjName");
            // Read recalculations
            Boolean isRecalculations = Objects.requireNonNullElse(jsonObject.optBoolean("isRecalculations"), false);
            Boolean isOutliers = Objects.requireNonNullElse(jsonObject.optBoolean("isOutliers"), false);
            String descAnalysis = qcAnalyticsHelper.getSpecsAnalysisDescription(isRecalculations, isOutliers);
            if ("running".equalsIgnoreCase(record.getOutlierJobStatus())
                    || "running".equalsIgnoreCase(record.getRecalcJobStatus())) {
                isJobRunning = true;
            }
            viewerDtos
                    .add(new QcAnalyticsViewerDto(record.getViewerId(), record.getFolderName(), record.getCreatedDate(),
                            record.getViewerCreateUser().getFirstName() + " "
                                    + record.getViewerCreateUser().getLastName(),
                            baselineYrLayer, baselineObject, descAnalysis,
                            isRecalculations ? record.getRecalcJobStatus() : "NA",
                            isRecalculations ? qcAnalyticsHelper.getSpecsRecalcYears(jsonObject) : null,
                            isOutliers ? record.getOutlierJobStatus() : "NA", record.getSpecifications()));
        }
        resultMap.put("viewerRecords", viewerDtos);
        resultMap.put("isJobRunning", isJobRunning);
        return resultMap;
    }

    @GetMapping("/refresh")
    public Map<String, Object> getViewerStatus() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        boolean isJobRunning = false;
        List<QcAnalyticsViewerDto> viewerDtos = analyticsViewerRepository.getViewerStatuses();
        for (QcAnalyticsViewerDto dto : viewerDtos) {
            if ("running".equalsIgnoreCase(dto.getOutlierJobStatus())
                    || "running".equalsIgnoreCase(dto.getRecalcJobStatus())) {
                isJobRunning = true;
                break;
            }
        }
        resultMap.put("viewerRecords", viewerDtos);
        resultMap.put("isJobRunning", isJobRunning);
        return resultMap;
    }

    @GetMapping("/nodes")
    public Map<String, Object> getDataNodes(@RequestParam(value = "parentId", required = true) String parentId,
            @RequestParam(value = "folderName", required = true) String folderName) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String fileName = folderName + "-data.json";
        qcService.downloadFile("analytics/qc/" + folderName + "/recalculations/aggregate_results.json", fileName);
        ArrayNode dataObject = qcService.extractDataNodes(fileName,
                parentId.isEmpty() ? null : parentId);
        resultMap.put("dataObject", dataObject);
        return resultMap;
    }

    @GetMapping("/rawData")
    public JsonNode getRawData(@RequestParam(value = "key", required = true) String rawDataKey,
            @RequestParam(value = "folderName", required = true) String folderName) throws Exception {
        String fileName = folderName + "-rawData.json";
        qcService.downloadFile("analytics/qc/" + folderName + "/recalculations/raw_results.json", fileName);
        JsonNode dataObject = qcService.extractRawData(fileName, rawDataKey);
        return dataObject;
    }

    @GetMapping("/downloadExcel")
    public ResponseEntity<StreamingResponseBody> downloadExcel(
            @RequestParam(value = "folderName", required = true) String folderName,
            @RequestParam(name = "user") int userId) throws Exception {
        // call python service for excel download
        String uriWithParams = String.format(
                "/qc_analytics/download-recalculations-excel?qca_object_handle=%s&user_id=%s",
                "analytics/qc/" + folderName,
                userId);
        log.debug("Qc Analytics download excel: ", uriWithParams);
        return restService.invokeRestClientExcelDownload(uriWithParams, "qc_analytics_" +
                folderName);
    }
}
