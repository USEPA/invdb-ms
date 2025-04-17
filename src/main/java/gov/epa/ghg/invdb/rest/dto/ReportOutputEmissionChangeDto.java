package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportOutputEmissionChangeDto {
    private Long emissionChangeId;
    private Long reportRowId;
    private Integer startYear;
    private Integer midYear;
    private Integer endYear;
    private Float startToMidAbsolute;
    private Float startToMidPercent;
    private Float startToEndAbsolute;
    private Float startToEndPercent;
    private Float midToEndAbsolute;
    private Float midToEndPercent;
    private Float prevToEndAbsolute;
    private Float prevToEndPercent;
    private Float startToMidOfTotalPercent;
    private Float startToEndOfTotalPercent;
    private Float midToEndOfTotalPercent;
    private Float prevToEndOfTotalPercent;
    private Float startToMidOfGasPercent;
    private Float startToEndOfGasPercent;
    private Float midToEndOfGasPercent;
    private Float prevToEndOfGasPercent;
    private Float endYrGasPercent;
    private Float endYrAllEmissionsPercent;
}
