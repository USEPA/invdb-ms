package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportDto {
    private Long reportId;
    private String reportName;
    private Date processedDate;
    private String processedBy;
    private String attachmentName;
    private Integer reportingYear;
    private String loadedBy;
    private Date loadedDate;
    private Boolean hasError;
    private String validationStatus;

    public ReportDto(Long reportId, String reportName) {
        this.reportId = reportId;
        this.reportName = reportName;
    }
}