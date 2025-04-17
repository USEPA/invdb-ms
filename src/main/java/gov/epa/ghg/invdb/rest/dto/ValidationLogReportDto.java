package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationLogReportDto {
    private Long logId;
    private String tabName;
    private String value;
    private Integer rowNumber;
    private String explanation;
}