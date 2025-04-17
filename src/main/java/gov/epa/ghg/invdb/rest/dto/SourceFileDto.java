package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SourceFileDto {
    private Long sourceFileId;
    private Integer sourceNameId;
    private String sourceName;
    private String sectorName;
    private Date processedDate;
    private Long attachmentId;
    private String attachmentName;
    private Integer reportingYear;
    private String loadedBy;
    private Date loadedDate;
    private Boolean hasError;
    private String validationStatus;
    private Date lastAttchLinkedDt;

    public SourceFileDto(Long sourecFileId, Integer sourceNameId) {
        this.sourceFileId = sourecFileId;
        this.sourceNameId = sourceNameId;
    }
}