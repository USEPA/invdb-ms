package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportOutputArCompDto {
    private Long reportRowId;
    private Integer reportOutputYearId;
    private Float reportOutputValue;
    private Float differenceValue;
    private Float percentValue;
}
