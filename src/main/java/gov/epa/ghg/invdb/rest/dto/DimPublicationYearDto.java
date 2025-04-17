package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimPublicationYearDto {
    private Integer pubYearId;
    private Integer pubYear;
    private String gwpColumn;
    private Integer maxTimeSeries;
}
