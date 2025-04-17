package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;

import gov.epa.ghg.invdb.model.DimQcReport;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimQcReportDto {

    private Integer reportId;
    private String reportName;
    private String reportTitle;
    private String reportRowsHeader;
    private Date reportRefreshDate;
    private String refreshStatus;
    private Integer reportingYear;
    private Integer layerId;

    public DimQcReportDto(DimQcReport report) {
        this.reportId = report.getReportId();
        this.reportName = report.getReportName();
        this.reportTitle = report.getReportTitle();
        this.reportRowsHeader = report.getReportRowsHeader();
        this.reportRefreshDate = report.getReportRefreshDate();
        this.refreshStatus = report.getRefreshStatus();
        this.reportingYear = report.getReportingYear();
        this.layerId = report.getLayerId();
    }
}
