package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArchivePackageRetrieveDto {
    private Integer id;
    private String archiveName;
    private String archiveDesc;
    private String eventTypeName;
    private Integer archiveAttachmentId;
    private Integer[] srcfileAttachmentIds;
    private Date lastCreatedDate;
    private String lastCreatedBy;
}
