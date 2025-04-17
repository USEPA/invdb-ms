package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArchiveObjectYearLayerDto {
    private Integer id;
    private String object_name;
    private Integer year;
    private Integer layer;
    private String archive_name;
}
