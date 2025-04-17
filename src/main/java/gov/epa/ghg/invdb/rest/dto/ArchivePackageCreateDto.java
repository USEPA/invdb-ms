package gov.epa.ghg.invdb.rest.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArchivePackageCreateDto {
    private Integer archivePackageId;
    private String archiveName;
    private String archiveDesc;
    private Integer eventTypeId;
    private List<ArchiveObjectDto> archiveObjects;
}
