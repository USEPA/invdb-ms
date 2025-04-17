package gov.epa.ghg.invdb.rest.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.epa.ghg.invdb.model.QcAnalyticsViewer;
import gov.epa.ghg.invdb.repository.ArchiveObjectRepository;
import gov.epa.ghg.invdb.repository.QcAnalyticsViewerRepository;
import gov.epa.ghg.invdb.rest.dto.QcAnalyticsBaselineDto;
import gov.epa.ghg.invdb.rest.dto.QcAnalyticsFormDto;
import gov.epa.ghg.invdb.rest.helper.QcAnalyticsHelper;
import gov.epa.ghg.invdb.service.QcAnalyticsService;
import gov.epa.ghg.invdb.service.S3Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/qualityAnalytics")
@Log4j2
public class QcAnalyticsToolController {
    @Autowired
    private QcAnalyticsService qcService;
    @Autowired
    private ArchiveObjectRepository archiveObjectRepository;
    @Autowired
    private QcAnalyticsViewerRepository analyticsViewerRepository;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private QcAnalyticsHelper qcAnalyticsHelper;

    @GetMapping("/getBaselineOptions")
    public Map<String, List<QcAnalyticsBaselineDto>> getBaselineOptions() {
        List<QcAnalyticsBaselineDto> baselineDtos = archiveObjectRepository.getQcAnalyticsBaselineOptions().stream()
                .map(row -> new QcAnalyticsBaselineDto((Integer) row[0], (String) row[1], ((Number) row[2]).longValue(),
                        (String) row[3], ((Number) row[4]).longValue(), (String) row[5]))
                .collect(Collectors.toList());
        Map<String, List<QcAnalyticsBaselineDto>> map = new LinkedHashMap<>();
        for (QcAnalyticsBaselineDto baselineDto : baselineDtos) {
            String key = "PY" + baselineDto.getYear() + " - " + baselineDto.getLayer();
            if (baselineDto.getArchivePkgName().equals("current")) {
                baselineDto.setDisplayName("Current");
            } else {
                String archiveObjDisp = baselineDto.getArchiveObjName().split("(?i)_refined")[0];
                String[] archivePkgDispList = baselineDto.getArchivePkgName().split("_");
                String displayName = String.format("%s from %s %s archive, taken on %s", archiveObjDisp,
                        archivePkgDispList[1], archivePkgDispList[2], archivePkgDispList[3]);
                baselineDto.setDisplayName(displayName);
            }
            map.computeIfAbsent(key, v -> new ArrayList<>()).add(baselineDto);
        }
        return map;
    }

    @GetMapping("/getCategories")
    public Map<String, List<Map<String, String>>> getCategories(
            @RequestParam(value = "archiveAttachmentId", required = true) Long archiveAttachmentId,
            @RequestParam(value = "objectName", required = true) String objectName,
            @RequestParam(name = "user") int userId) throws Exception {
        String filename = archiveAttachmentId + "-" + objectName;
        if (archiveAttachmentId > 0) {
            // extract json file from archive
            qcService.processPkgAttchAndExtractJson(filename, archiveAttachmentId);
        } else {
            // it is current data so not in archive. 0-{yearId}-{layerId}
            String keys[] = objectName.split("-");
            qcService.processCurrentAndExtractJson(filename, Integer.valueOf(keys[1]), Integer.valueOf(keys[2]),
                    userId);
        }
        // extract sector, sub-sector and category from json file
        Map<String, List<Map<String, String>>> resultMap = qcService.extractSecSubsecCatFromJson(filename);
        return resultMap;
    }

    @PostMapping("/generateTable")
    public ResponseEntity<String> generateTable(@RequestBody QcAnalyticsFormDto qcAnalyticsForm,
            @RequestParam(name = "user") int userId) throws Exception {
        // insert record into database
        String jsonString = qcService.generateSpecificationsJson(qcAnalyticsForm);
        byte[] specBytes = jsonString.getBytes();
        // qcService.uploadBytesToS3(specBytes, fold
        String folderName = qcService.getTimestampedFolderName();
        QcAnalyticsViewer viewer = new QcAnalyticsViewer();
        viewer.setFolderName(folderName);
        viewer.setSpecifications(jsonString);
        viewer.setRecalcJobStatus(qcAnalyticsForm.getIsRecalculations() ? "running" : "na");
        viewer.setOutlierJobStatus(qcAnalyticsForm.getIsTimeseriesOutlier() ? "running" : "na");
        viewer.setCreatedBy(userId);
        viewer.setCreatedDate(new Date());
        analyticsViewerRepository.save(viewer);
        qcAnalyticsHelper.handleGenerateTable(qcAnalyticsForm, folderName, specBytes, userId);
        return ResponseEntity.ok("success");
        // if (qcAnalyticsForm.getIsRecalculations()) {
        // String filepath = "";
        // String baselineFilename = qcAnalyticsForm.getBaselinePkgAttachmentId() + "-"
        // + qcAnalyticsForm.getBaselineObjName();
        // String compFilename = qcAnalyticsForm.getCompartorPkgAttachmentId() + "-"
        // + qcAnalyticsForm.getComparatorObjName();,
        // // 1. Extract Comparator file
        // if (qcAnalyticsForm.getCompartorPkgAttachmentId() > 0) {
        // qcService.processPkgAttchAndExtractJson(compFilename,
        // qcAnalyticsForm.getCompartorPkgAttachmentId());
        // } else {
        // // it is current data so not in archive. {yearId}-{layerId}
        // String keys[] = qcAnalyticsForm.getComparatorObjName().split("-");
        // qcService.processCurrentAndExtractJson(compFilename,
        // Integer.valueOf(keys[1]), Integer.valueOf(keys[2]),
        // userId);
        // }
        // // 2. if filterJson is true parse baseline and comparator files and extract
        // only
        // // those that match the selected categories
        // if (qcAnalyticsForm.getFilterJson() == true) {
        // filepath = "/filtered/";
        // // baseline json
        // qcService.processAndWriteToFile(
        // baselineFilename, qcAnalyticsForm.getCategories());
        // // comparator json
        // qcService.processAndWriteToFile(compFilename,
        // qcAnalyticsForm.getCategories());
        // }
        // // 3. upload specifications, baseline and compartor (if applicable) to s3
        // byte[] specBytes = jsonString.getBytes();
        // qcService.uploadBytesToS3(specBytes, folderName + "/metadata.json");
        // qcService.uploadFileToS3(filepath + baselineFilename, folderName +
        // "/baseline.json");
        // qcService.uploadFileToS3(filepath + compFilename, folderName +
        // "/comparator.json");

        // // call python service for recalculations
        // String uriWithParams =
        // String.format("/qc_analytics/recalculations-report?qca_object_handle=%s&&user_id=%s",
        // "analytics/qc/" + folderName,
        // userId);
        // log.debug("Source file load URL: ", uriWithParams);
        // return restService.invokeWebClient(uriWithParams);
        // }
    }

    @GetMapping("/listFilesInBucket")
    public void getQcData(HttpServletResponse response) throws Exception {
        String[] files = s3Service.listFilesInBucket("invdb-prod-data-files");
        for (String file : files) {
            System.out.println("file: " + file);
        }
    }

    @GetMapping("/verifyFiles")
    public ResponseEntity<String> verifyFiles(HttpServletResponse response) throws Exception {
        qcService.verifyS3Files("/metadata.json");
        qcService.verifyS3Files("/baseline.json");
        qcService.verifyS3Files("/comparator.json");
        return ResponseEntity.ok("success");
    }
}
