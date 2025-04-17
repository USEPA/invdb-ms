package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimSectorDto {
    private Integer sectorId;
    private String sectorCode;
    private String sectorName;
}