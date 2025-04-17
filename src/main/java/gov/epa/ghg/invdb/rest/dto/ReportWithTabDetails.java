package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportWithTabDetails {
    private Integer reportId;
    private String reportName;
    private String reportTitle;
    private String reportRowsHeader;
    private Date reportRefreshDate;
    private String refreshStatus;
    private String tabDetails;
}
