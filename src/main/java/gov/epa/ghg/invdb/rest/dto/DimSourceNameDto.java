package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimSourceNameDto {
    private Integer id;
    private String name;
    private Integer pubYearId;
}