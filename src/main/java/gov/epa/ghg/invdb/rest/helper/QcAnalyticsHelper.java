package gov.epa.ghg.invdb.rest.helper;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import gov.epa.ghg.invdb.rest.dto.QcAnalyticsFormDto;
import gov.epa.ghg.invdb.service.QcAnalyticsService;
import gov.epa.ghg.invdb.service.RestService;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class QcAnalyticsHelper {
    @Autowired
    private QcAnalyticsService qcService;
    @Autowired
    private RestService restService;

    public String getSpecsAnalysisDescription(boolean isRecalculations, boolean isOutliers) {
        StringBuilder sb = new StringBuilder();
        if (isRecalculations) {
            sb.append("Comparision with Prior Data, ");
        }
        if (isOutliers) {
            sb.append("Outlier, ");
        }
        // Remove the last comma and space, if any text was appended
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    public List<String> getSpecsRecalcYears(JSONObject jsonObject) throws Exception {
        List<String> outputYears = new ArrayList<>();
        // Access the recalcs object
        JSONObject recalcsObject = jsonObject.optJSONObject("recalculations");
        JSONArray outputYearsJsonArray = recalcsObject != null ? recalcsObject.optJSONArray("outputYears") : null;
        if (outputYearsJsonArray != null) {
            for (int i = 0; i < outputYearsJsonArray.length(); i++) {
                outputYears.add(String.valueOf(outputYearsJsonArray.getInt(i)));
            }
        }
        return outputYears;
    }

    @Async
    public void handleGenerateTable(QcAnalyticsFormDto qcAnalyticsForm, String folderName, byte[] specBytes, int userId)
            throws Exception {
        String filepath = "";
        String baselineFilename = qcAnalyticsForm.getBaselinePkgAttachmentId() + "-"
                + qcAnalyticsForm.getBaselineObjName();
        // 1. if filterJson is true parse baseline and extract only
        // those that match the selected categories
        if (qcAnalyticsForm.getFilterJson() == true) {
            filepath = "/filtered/";
            // baseline json
            qcService.processAndWriteToFile(baselineFilename,
                    qcAnalyticsForm.getCategories());
        }
        // 2. upload specifications and baseline to s3
        qcService.uploadBytesToS3(specBytes, folderName + "/metadata.json");
        qcService.uploadFileToS3(filepath + baselineFilename, folderName +
                "/baseline.json");
        if (qcAnalyticsForm.getIsRecalculations()) {
            handleRecalculation(qcAnalyticsForm, folderName, userId);
        }
    }

    public void handleRecalculation(QcAnalyticsFormDto qcAnalyticsForm, String folderName, int userId)
            throws Exception {
        String filepath = "";
        String compFilename = qcAnalyticsForm.getCompartorPkgAttachmentId() + "-"
                + qcAnalyticsForm.getComparatorObjName();
        // 1. Extract Comparator file
        if (qcAnalyticsForm.getCompartorPkgAttachmentId() > 0) {
            qcService.processPkgAttchAndExtractJson(compFilename, qcAnalyticsForm.getCompartorPkgAttachmentId());
        } else {
            // it is current data so not in archive. {yearId}-{layerId}
            String keys[] = qcAnalyticsForm.getComparatorObjName().split("-");
            qcService.processCurrentAndExtractJson(compFilename, Integer.valueOf(keys[1]), Integer.valueOf(keys[2]),
                    userId);
        }
        // 2. if filterJson is true parse comparator files and extract only
        // those that match the selected categories
        if (qcAnalyticsForm.getFilterJson() == true) {
            filepath = "/filtered/";
            // comparator json
            qcService.processAndWriteToFile(compFilename, qcAnalyticsForm.getCategories());
        }
        // 3. upload compartor to s3
        qcService.uploadFileToS3(filepath + compFilename, folderName + "/comparator.json");

        // call python service for recalculations
        String uriWithParams = String.format("/qc_analytics/recalculations-report?qca_object_handle=%s&user_id=%s",
                "analytics/qc/" + folderName,
                userId);
        log.debug("Qc analytics recals URL: ", uriWithParams);
        Mono<ResponseEntity<String>> response = restService.invokeWebClient(uriWithParams);
        response.subscribe(res -> {
            // process the response if needed
        }, error -> {
            // handle error if needed
            error.printStackTrace();
        });
    }

}
