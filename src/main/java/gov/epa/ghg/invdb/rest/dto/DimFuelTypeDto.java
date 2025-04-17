package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimFuelTypeDto {
    private Integer fuelTypeId;
    private String fuelTypeCode;
    private String fuelTypeName;
}