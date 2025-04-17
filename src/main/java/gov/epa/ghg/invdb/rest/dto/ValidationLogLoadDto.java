package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationLogLoadDto {
    private Long logId;
    private String fieldName;
    private String fieldValue;
    private Integer rowNumber;
    private String explanation;
}