package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;
import java.util.List;

import gov.epa.ghg.invdb.model.DimReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DimReportDto {

    private Integer reportId;
    private String reportName;
    private String reportTitle;
    private String reportRowsHeader;
    private Date reportRefreshDate;
    private String refreshStatus;
    private Integer reportingYear;
    private Integer layerId;
    private List<Tab> tabs;

    public DimReportDto(DimReport report) {
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
