package gov.epa.ghg.invdb.rest.dto;
import gov.epa.ghg.invdb.rest.dto.ValidationLogLoadDto;
import gov.epa.ghg.invdb.rest.dto.ValidationLogExtractDto;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationLogWrapperDto {
    private List<ValidationLogLoadDto> validationLogLoadDtos;
    private List<ValidationLogExtractDto> validationLogExtractDtos;
}