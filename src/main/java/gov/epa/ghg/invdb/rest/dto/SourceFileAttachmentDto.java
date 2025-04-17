package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SourceFileAttachmentDto {
    private Long attachmentId;
    private String attachmentName;
    private Date loadedDate;
    private String loadedBy;
    private Boolean hasError;
    private java.util.Date processedDate;
    private java.util.Date lastSrcfileLinkedDt;
}
