package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportOutputDto {
    private Long reportOutputId;
    private Long reportRowId;
    private Integer reportOutputYearId;
    private Float reportOutputValue;
    private Integer startYear;
    private Integer endYear;
    private Float startToEndPercent;
    private Float endYrAllEmissionsPercent;

    public ReportOutputDto(Long reportOutputId, Long reportRowId, Integer reportOutputYearId, Float reportOutputValue) {
        this.reportOutputId = reportOutputId;
        this.reportRowId = reportRowId;
        this.reportOutputYearId = reportOutputYearId;
        this.reportOutputValue = reportOutputValue;
    }
}
