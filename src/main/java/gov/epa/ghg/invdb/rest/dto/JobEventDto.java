package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobEventDto {
    private String jobName;
    private String jobStatus;
    private Date jobEndTime;
    private String eventName;
    private String eventDetails;
    private Date eventDate;
}