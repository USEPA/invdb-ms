package gov.epa.ghg.invdb.rest.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.epa.ghg.invdb.enumeration.AttachmentType;
import gov.epa.ghg.invdb.model.Report;
import gov.epa.ghg.invdb.model.ValidationLogReport;
import gov.epa.ghg.invdb.repository.ReportRepository;
import gov.epa.ghg.invdb.repository.ValidationLogReportRepository;
import gov.epa.ghg.invdb.rest.dto.AttachmentDto;
import gov.epa.ghg.invdb.rest.dto.DimExcelReportDto;
import gov.epa.ghg.invdb.rest.dto.ReportDto;
import gov.epa.ghg.invdb.rest.dto.ValidationLogReportDto;
import gov.epa.ghg.invdb.rest.helper.AttachmentHelper;
import gov.epa.ghg.invdb.service.ReportService;
import gov.epa.ghg.invdb.service.RestService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reports")
@Log4j2
public class ReportController {
        @Autowired
        private ReportRepository reportRepository;
        @Autowired
        private ValidationLogReportRepository validationLogRepository;
        @Autowired
        private ReportService reportService;
        @Autowired
        private AttachmentHelper attachmentHelper;
        @Autowired
        private RestService restService;

        @GetMapping("/load")
        public List<ReportDto> getReports(@RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int rptYear) {
                List<ReportDto> reports = reportRepository.getFilesForDisplayByLayerAndYear(layerId, rptYear);
                return reports;
        }

        @PostMapping("/save")
        public ResponseEntity<String> saveReports(@RequestParam("files") List<MultipartFile> files,
                        @RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int rptYear,
                        @RequestParam(name = "user") int userId)
                        throws Exception {
                // get existing reports
                List<ReportDto> existingReports = reportRepository.getFilesWithRptNameByLayerAndYear(layerId,
                                rptYear);
                reportService.save(files, existingReports, layerId, rptYear, userId);
                // invoke data validation service
                String uriWithParams = String.format("/report-validation?reporting_year=%s&layer_id=%s&user_id=%s",
                                rptYear,
                                layerId,
                                userId);
                log.debug("Report validate URL: ", uriWithParams);
                return restService.invokeRestClient(uriWithParams);
        }

        @GetMapping("/download")
        public void downloadAttachments(
                        @RequestParam(name = "reportIds") List<Long> reportIds, HttpServletResponse response)
                        throws IOException {
                List<Report> reports = reportRepository.findAllById(reportIds);
                List<AttachmentDto> attachementDtos = reports.stream()
                                .map(report -> new AttachmentDto(report.getReportId(),
                                                report.getAttachmentName(),
                                                AttachmentType.EXCEL, report.getAttachmentSize(),
                                                report.getContent()))
                                .collect(Collectors.toList());
                attachmentHelper.downloadZip(response, "reports", attachementDtos);
        }

        @GetMapping("/loadErrorLogs")
        public List<ValidationLogReportDto> getReportErrors(@RequestParam(name = "reportId") Long reportId) {
                List<ValidationLogReport> validationLogs = validationLogRepository
                                .findByReportIdOrderByRowNumberAsc(reportId);
                List<ValidationLogReportDto> validationLogDtos = validationLogs.stream()
                                .map(log -> new ValidationLogReportDto(log.getLogId(), log.getTabName(),
                                                log.getValue(),
                                                log.getRowNumber(),
                                                log.getDescription()))
                                .collect(Collectors.toList());
                return validationLogDtos;
        }

        @PostMapping("/process")
        public Mono<ResponseEntity<String>> process(@RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int rptYear, @RequestParam(name = "user") int userId)
                        throws Exception {
                String uriWithParams = String.format("/report-processing?reporting_year=%s&layer_id=%s&user_id=%s",
                                rptYear,
                                layerId,
                                userId);
                log.debug("Process Reports URL: ", uriWithParams);
                return restService.invokeWebClient(uriWithParams);
        }

        @PostMapping("/createFromExcel")
        public ResponseEntity<Object> createFromExcel(@RequestBody DimExcelReportDto excelReport,
                        @RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int rptYear,
                        @RequestParam(name = "user") int userId)
                        throws Exception {
                // get existing reports
                List<ReportDto> existingReports = reportRepository.getFilesWithRptNameByLayerAndYear(layerId,
                                rptYear);
                Long reportId = reportService.createFromExcel(excelReport, existingReports, layerId, rptYear, userId);
                if (reportId > 0) {
                        return ResponseEntity.status(HttpStatus.CREATED).body(reportId);
                } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("An error when saving report");
                }

        }

        @PostMapping("/delete")
        public ResponseEntity<String> deleteReports(
                        @RequestParam("reportIds") Long[] reportIds,
                        @RequestParam(name = "user") int userId)
                        throws Exception {
                ResponseEntity<String> response = null;
                try {
                        reportRepository.deleteAllById(Arrays.asList(reportIds));
                        response = ResponseEntity.ok("Files deleted successfully. ");
                } catch (Exception e) {
                        response = ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(e.getCause() + ": " + e.getMessage());
                }
                return response;
        }

}
