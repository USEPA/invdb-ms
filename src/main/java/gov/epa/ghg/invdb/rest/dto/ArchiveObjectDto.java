package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArchiveObjectDto {
    private String objectName;
    private String objectType;
    private Long objectAttachmentId;
    private Date lastUpdatedDate;
    private int lastUpdatedBy;
}
