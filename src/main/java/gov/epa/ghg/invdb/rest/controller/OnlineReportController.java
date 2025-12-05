package gov.epa.ghg.invdb.rest.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import gov.epa.ghg.invdb.enumeration.AttachmentType;
import gov.epa.ghg.invdb.model.DimReport;
import gov.epa.ghg.invdb.repository.DimGhgRepository;
import gov.epa.ghg.invdb.repository.DimReportRepository;
import gov.epa.ghg.invdb.repository.DimReportRowRepository;
import gov.epa.ghg.invdb.repository.ReportOutputEmissionChangeRepository;
import gov.epa.ghg.invdb.repository.ReportOutputKilotonsRepository;
import gov.epa.ghg.invdb.repository.ReportOutputRepository;
import gov.epa.ghg.invdb.repository.SourceFileAttachmentRepository;
import gov.epa.ghg.invdb.rest.dto.AttachmentDto;
import gov.epa.ghg.invdb.rest.dto.DimReportDto;
import gov.epa.ghg.invdb.rest.dto.DimReportRowDto;
import gov.epa.ghg.invdb.rest.dto.ReportOutputArCompDto;
import gov.epa.ghg.invdb.rest.dto.ReportOutputDto;
import gov.epa.ghg.invdb.rest.dto.ReportOutputEmissionChangeDto;
import gov.epa.ghg.invdb.rest.dto.ReportOutputGwpCompDto;
import gov.epa.ghg.invdb.rest.helper.AttachmentHelper;
import gov.epa.ghg.invdb.service.OnlineReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/onlineReport")
@Log4j2
public class OnlineReportController {
    @Autowired
    private DimReportRepository dimReportRepository;
    @Autowired
    private SourceFileAttachmentRepository sourceFileAttachmentRepository;
    @Autowired
    private OnlineReportService reportService;
    @Autowired
    private AttachmentHelper attachmentHelper;
    @Autowired
    private DimReportRowRepository dimReportRowRepository;
    @Autowired
    private ReportOutputRepository reportOutputRepository;
    @Autowired
    private ReportOutputEmissionChangeRepository reportOutputEmissionChangeRepository;
    @Autowired
    private ReportOutputKilotonsRepository reportOutputKilotonsRepository;
    @Autowired
    private DimGhgRepository dimGhgRepository;

    @GetMapping("/dataAge")
    public Date getReportsDataAge(@RequestParam(name = "layer") int layerId,
            @RequestParam(name = "year") int rptYear) {
        Date reportDataAgeTimestamp = sourceFileAttachmentRepository.findReportDataAge(layerId, rptYear);
        return reportDataAgeTimestamp;
    }

    @GetMapping("/refresh")
    public DimReportDto generateReport(@RequestParam(name = "reportId") Long reportId,
            @RequestParam(name = "user") int userId)
            throws Exception {
        if (userId == 0) {
            throw new Exception("Unrecognized user");
        }
        DimReportDto refreshedReport = new DimReportDto(reportService.refreshOnlineReport(reportId, userId));
        return refreshedReport;
    }

    @GetMapping("/getReportById")
    public DimReportDto getReportById(@RequestParam(name = "reportId") Long reportId) throws Exception {
        return reportService.getReportById(reportId);
    }

    @GetMapping("/load")
    public List<DimReportRowDto> getOnlineReportData(@RequestParam(name = "reportId") Long reportId) {
        List<DimReportRowDto> reportRows = null;
        reportRows = reportService.getOnlineReportData(reportId);
        return reportRows;
    }

    @GetMapping("/download")
    public void downloadAttachments(
            @RequestParam(name = "dimReportId") Long[] dimReportIds,
            @RequestParam(name = "format") String format,
            @RequestParam(name = "user") int userId,
            HttpServletResponse response)
            throws IOException {
        if (dimReportIds == null || ArrayUtils.isEmpty(dimReportIds)) {
            throw new IOException("dimReportId list is empty");
        }
        Long reportId = dimReportIds[0];
        DimReport report = dimReportRepository.findById(reportId).get();
        List<AttachmentDto> attachementDtos = new ArrayList<AttachmentDto>();

        if (format.equalsIgnoreCase(AttachmentType.JSON.name())) {
            String reportDataString = reportService.generateEmissionsReportJson(reportId);
            attachementDtos.add(new AttachmentDto(report.getReportTitle(), reportDataString, AttachmentType.JSON));
        } else if (format.equalsIgnoreCase(AttachmentType.EXCEL.name())) {
            byte[] reportData = reportService.generateEmissionsReportExcel(reportId);
            attachementDtos.add(new AttachmentDto(report.getReportTitle(), reportData, AttachmentType.EXCEL));
        } else if (format.equalsIgnoreCase(AttachmentType.CSV.name())) {
            String reportDataString = reportService.generateEmissionsReportCsv(reportId);
            attachementDtos.add(new AttachmentDto(report.getReportTitle(), reportDataString, AttachmentType.CSV));
        } else if (format.equalsIgnoreCase(AttachmentType.PDF.name())) {
            log.warn("Unimplemented feature PDF Report Download requested. Skipping...");
        }
        attachmentHelper.downloadZip(response, "EmissionsReport_" + report.getReportTitle(), attachementDtos);

    }

    @SuppressWarnings("unchecked")
    @PostMapping("/downloadRpt")
    public ResponseEntity<StreamingResponseBody> downloadRpt(
            @RequestBody Map<String, Object> requestBody,
            @RequestParam(name = "format") String format,
            @RequestParam(name = "user") int userId)
            throws Exception {
        List<Map<String, Object>> headers = (List<Map<String, Object>>) requestBody.get("headers");
        List<Map<String, Object>> data = (List<Map<String, Object>>) requestBody.get("data");
        byte[] reportData = reportService.generateEmissionsReportForDownload(headers,
                data, format);
        return attachmentHelper.createFileDownloadResponse(reportData, "abc",
                format);
    }

    @GetMapping("/getReportRowsMetadata")
    public List<DimReportRowDto> getReportRowsMetadata(@RequestParam(name = "reportId") Long reportId)
            throws Exception {
        List<DimReportRowDto> reportRows = dimReportRowRepository.findByReportId(reportId);
        return reportRows;
    }

    @GetMapping("/getEmissionsMMTYr")
    public List<ReportOutputDto> getEmissionsMMTYr(@RequestParam(name = "reportId") Long reportId)
            throws Exception {
        List<ReportOutputDto> reportData = reportOutputRepository.findRptOpWithPercentDets(reportId);
        return reportData;
    }

    @GetMapping("/getEmissionsChange")
    public List<ReportOutputEmissionChangeDto> getEmissionsChange(@RequestParam(name = "reportId") Long reportId)
            throws Exception {
        List<ReportOutputEmissionChangeDto> reportData = reportOutputEmissionChangeRepository.findByReportId(reportId);
        return reportData;
    }

    @GetMapping("/getEmissionsKilotonYr")
    public List<ReportOutputDto> getEmissionsKilotonYr(@RequestParam(name = "reportId") Long reportId)
            throws Exception {
        List<ReportOutputDto> reportData = reportOutputKilotonsRepository.findByReportId(reportId);
        return reportData;
    }

    @GetMapping("/getArComparisonData")
    public List<ReportOutputArCompDto> getArComparisonData(@RequestParam(name = "reportId") Long reportId,
            @RequestParam(name = "arOption") String arOption,
            @RequestParam(name = "user") int userId)
            throws Exception {
        if (userId == 0) {
            throw new Exception("Unrecognized user");
        }
        List<ReportOutputArCompDto> arCompDtos = reportService.getArComparisonData(reportId, arOption, userId);
        return arCompDtos;
    }

    @GetMapping("/getGwpComparisonData")
    public List<ReportOutputGwpCompDto> getGwpComparisonData(@RequestParam(name = "reportId") Long reportId)
            throws Exception {
        List<ReportOutputGwpCompDto> gwpCompDtos = dimGhgRepository.findGhgComparisonDetails(reportId);
        return gwpCompDtos;
    }
}
