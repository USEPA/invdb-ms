package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// @TODO -- delete this file
@Data
@AllArgsConstructor
public class FactsArchiveYearLayerDto {
    private Integer year;
    private Integer layer;
}
