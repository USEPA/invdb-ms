package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimSubsectorDto {
    private Integer subsectorId;
    private String subsectorCode;
    private String subsectorName;
    private Integer sectorId;

}