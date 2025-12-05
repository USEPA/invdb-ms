package gov.epa.ghg.invdb.rest.dto;

import gov.epa.ghg.invdb.model.DimSubsector;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimCategoryDto {
    public DimCategoryDto(Integer categoryId, String categoryCode, String categoryName, Integer subsectorId,
			String categoryActive) {
		super();
		this.categoryId = categoryId;
		this.categoryCode = categoryCode;
		this.categoryName = categoryName;
		this.subsectorId = subsectorId;
		this.categoryActive = categoryActive;
	}
	private Integer categoryId;
    private String categoryCode;
    private String categoryName;
    private Integer subsectorId;
    private String categoryActive;
    private String subsectorName;
}