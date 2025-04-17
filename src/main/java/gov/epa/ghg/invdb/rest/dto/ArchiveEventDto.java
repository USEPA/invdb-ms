package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArchiveEventDto {
    private Integer eventTypeId;
    private String eventName;
    private String eventAbbr;
    private Integer archivePackageId;
}
