package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimSourceNameDto {
    
	public DimSourceNameDto() {}
	public DimSourceNameDto(Integer id, String name, Integer pubYearId) {
		super();
		this.id = id;
		this.name = name;
		this.pubYearId = pubYearId;
	}

	private Integer id;
	private String name;
	private Integer pubYearId;
	private Integer sectorId;
	private Integer categoryId;
	private String subCategory1;


}