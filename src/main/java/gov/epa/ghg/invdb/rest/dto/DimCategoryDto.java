package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimCategoryDto {
    private Integer categoryId;
    private String categoryCode;
    private String categoryName;
    private Integer subsectorId;
}