package gov.epa.ghg.invdb.rest.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.epa.ghg.invdb.enumeration.AttachmentType;
import gov.epa.ghg.invdb.model.DimQcReport;
import gov.epa.ghg.invdb.repository.DimQcReportRepository;
import gov.epa.ghg.invdb.repository.SourceFileAttachmentRepository;
import gov.epa.ghg.invdb.rest.dto.AttachmentDto;
import gov.epa.ghg.invdb.rest.dto.DimQcCompReportRowDto;
import gov.epa.ghg.invdb.rest.dto.DimQcReportDto;
import gov.epa.ghg.invdb.rest.helper.AttachmentHelper;
import gov.epa.ghg.invdb.service.QcReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/qc")
@Log4j2
public class QcController {
    @Autowired
    private QcReportService qcReportService;
    @Autowired
    private SourceFileAttachmentRepository sourceFileAttachmentRepository;
    @Autowired
    private DimQcReportRepository dimQcReportRepository;
    @Autowired
    private AttachmentHelper attachmentHelper;

    @GetMapping("/qcReports")
    public List<DimQcReportDto> getQcReports(@RequestParam(name = "layer") int layerId,
            @RequestParam(name = "year") int rptYear) {
        List<DimQcReportDto> reportDtos = dimQcReportRepository.findReportDtos(layerId, rptYear);
        return reportDtos;
    }

    @GetMapping("/getQcReportById")
    public DimQcReportDto getReportById(@RequestParam(name = "reportId") Long reportId) throws Exception {
        return qcReportService.getReportById(reportId);
    }

    @GetMapping("/qcReportsDataAge")
    public Date getQcReportsDataAge(@RequestParam(name = "layer") int layerId,
            @RequestParam(name = "year") int rptYear) {
        Date reportDataAgeTimestamp = sourceFileAttachmentRepository.findReportDataAge(layerId, rptYear);
        return reportDataAgeTimestamp;
    }

    // @GetMapping("/qcReportsYears")
    // public List<TimeSeriesDto> getAllQcReportingYears() {
    // List<DimTimeSeries> years = dimTimeSeriesRepository.findAll();
    // Collections.sort(years, (p1, p2) -> {
    // return (Long.valueOf(p1.getYearId() - p2.getYearId())).intValue();
    // });
    // return years.stream().map(yr -> new TimeSeriesDto(yr.getYearId(),
    // yr.getYear())).collect(Collectors.toList());
    // }

    @GetMapping("/refreshQcReport")
    public DimQcReportDto generateQcReport(@RequestParam(name = "reportId") Long reportId,
            @RequestParam(name = "userId") int userId) throws Exception {
        if (userId == 0) {
            throw new Exception("Unrecognized user");
        }
        DimQcReportDto refreshedReport = new DimQcReportDto(qcReportService.refreshQcReport(reportId, userId));
        return refreshedReport;
    }

    @GetMapping("/qcReportData")
    public List<DimQcCompReportRowDto> getQcReportData(@RequestParam(name = "reportId") Long reportId) {
        List<DimQcCompReportRowDto> reportRows = qcReportService.getQcReportData(reportId);
        return reportRows;
    }

    @GetMapping("/qcdownload")
    public void downloadQcReport(
            @RequestParam(name = "dimReportId") Long[] dimReportIds,
            @RequestParam(name = "format") String format,
            HttpServletResponse response)
            throws IOException {
        if (dimReportIds == null || ArrayUtils.isEmpty(dimReportIds)) {
            throw new IOException("pubObjIds list is empty");
        }
        Long reportId = dimReportIds[0];
        List<AttachmentDto> attachementDtos = new ArrayList<AttachmentDto>();
        DimQcReport report = dimQcReportRepository.findById(reportId).get();

        if (format.equalsIgnoreCase(AttachmentType.JSON.name())) {
            String reportDataString = qcReportService.generateQcReportJson(reportId);
            attachementDtos.add(new AttachmentDto(report.getReportTitle(), reportDataString, AttachmentType.JSON));
        } else if (format.equalsIgnoreCase(AttachmentType.EXCEL.name())) {
            byte[] reportData = qcReportService.generateQcReportExcel(reportId);
            attachementDtos.add(new AttachmentDto(report.getReportTitle(), reportData, AttachmentType.EXCEL));
        } else if (format.equalsIgnoreCase(AttachmentType.CSV.name())) {
            String reportDataString = qcReportService.generateQcReportCsv(reportId);
            attachementDtos.add(new AttachmentDto(report.getReportTitle(), reportDataString, AttachmentType.CSV));
        } else if (format.equalsIgnoreCase(AttachmentType.PDF.name())) {
            log.warn("Unimplemented feature PDF Report Download requested. Skipping...");
        }
        attachmentHelper.downloadZip(response, "EmissionsReport_" + report.getReportTitle(), attachementDtos);
    }

}
