package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QcCompReportOutputDto {
    private Long reportOutputId;
    private Long reportRowId;
    private Integer reportOutputYearId;
    private Float reportOutputValue;
}
