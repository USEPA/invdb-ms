package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SourceFileSummaryDto {
    private Integer missingFiles;
    private Integer filesWithErrors;
    private Integer filesWithoutErrors;
    private Date lastUploadDate;
    private String lastUploadBy;
    private Date lastProcessedDate;
    private String lastProcessedBy;
}
