package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimGhgCategoryDto {
    private Integer ghgCategoryId;
    private String ghgCategoryCode;
    private String ghgCategoryName;
}