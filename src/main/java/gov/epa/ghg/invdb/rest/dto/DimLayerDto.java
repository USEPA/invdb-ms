package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimLayerDto {
    private Integer id;
    private String name;
    private String displayName;
    private Integer defaultYear;
    private Integer defaultYearId;
}