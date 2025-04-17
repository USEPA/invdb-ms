package gov.epa.ghg.invdb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import gov.epa.ghg.invdb.enumeration.ValidationStatus;
import gov.epa.ghg.invdb.model.Report;
import gov.epa.ghg.invdb.repository.ReportRepository;
import gov.epa.ghg.invdb.rest.dto.DimExcelReportDto;
import gov.epa.ghg.invdb.rest.dto.ReportDto;
import gov.epa.ghg.invdb.util.ExcelUtil;
import gov.epa.ghg.invdb.util.FileUtil;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private ExcelUtil excelUtil;
    @Autowired
    private FileUtil fileUtil;

    @Transactional
    public void save(List<MultipartFile> files, List<ReportDto> existingReports,
            int layerId, int reportingYr, int userId)
            throws Exception {
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            Report report = null;
            Date today = new Date();
            byte[] fileContent = fileUtil.getFilebytes(file);

            String reportName = excelUtil.readCell(file, "Table", 2, 2).orElse("");
            // Check if there is already an existing record for this report
            ReportDto existingRpt = existingReports.stream()
                    .filter(f -> f.getReportName().equals(reportName)).findFirst().orElse(null);
            if (existingRpt != null) {
                report = reportRepository.findById(existingRpt.getReportId()).orElse(null);
            }
            if (report == null) {
                report = new Report();
                report.setReportName(reportName);
                report.setReportingYear(reportingYr);
                report.setLayerId(layerId);
                report.setCreatedBy(userId);
                report.setCreatedDate(today);
            }
            report.setLastUploadedBy(userId);
            report.setLastUploadedDate(today);
            report.setLastUpdatedBy(userId);
            report.setLastUpdatedDate(today);
            report.setValidationStatus(null);
            report.setAttachmentName(fileName);
            report.setAttachmentType(file.getContentType());
            report.setAttachmentSize(file.getSize());
            report.setContent(fileContent);
            report.setHasError(false);
            report.setProcessedDate(null);
            reportRepository.save(report);
        }
    }

    @Transactional
    public Long createFromExcel(DimExcelReportDto excelReport, List<ReportDto> existingReports, int layerId,
            int reportingYr, int userId)
            throws Exception {
        Report report = null;
        Date today = new Date();
        // Check if there is already an existing record for this report
        ReportDto existingRpt = existingReports.stream()
                .filter(f -> f.getReportName().equals(excelReport.getReportName())).findFirst().orElse(null);
        if (existingRpt != null) {
            report = reportRepository.findById(existingRpt.getReportId()).orElse(null);
        }
        if (report == null) {
            report = new Report();
            report.setReportName(excelReport.getReportName());
            report.setReportingYear(reportingYr);
            report.setLayerId(layerId);
            report.setCreatedBy(userId);
            report.setCreatedDate(today);
        }
        report.setLastUploadedBy(userId);
        report.setLastUploadedDate(today);
        report.setLastUpdatedBy(userId);
        report.setLastUpdatedDate(today);
        report.setAttachmentSize(excelReport.getFileSize());
        report.setAttachmentType(excelReport.getFileType());
        report.setValidationStatus(ValidationStatus.SUCCESS.getValue());
        report.setAttachmentName(excelReport.getFilename());
        report.setHasError(false);
        report.setProcessedDate(null);
        Report rpt = reportRepository.save(report);
        if (rpt == null || rpt.getReportId() == null) {
            throw new Exception("Error occured saving report");
        }
        reportRepository.updateReportWithExcel(excelReport.getId(), rpt.getReportId());
        return rpt.getReportId();
    }

}
