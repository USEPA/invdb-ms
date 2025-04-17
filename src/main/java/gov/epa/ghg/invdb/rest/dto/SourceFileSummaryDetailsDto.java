package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SourceFileSummaryDetailsDto {
    private String filetype;
    private String sourceName;
    private String sectorName;
    private Date lastUploadDate;
    private String lastUploadBy;
    private String filename;
}
