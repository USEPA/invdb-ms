package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationLogExtractDto {
    private Long logId;
    private Long emissionsQcLoadTargetId;
    private String cellValue;
    private String cellLocation;
    private String description;
}