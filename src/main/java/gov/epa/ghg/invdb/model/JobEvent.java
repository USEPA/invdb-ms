package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import java.util.Date;

import gov.epa.ghg.invdb.rest.dto.JobEventDto;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "job_event")
@NamedNativeQuery(name = "get_job_events", query = "select jl.job_name \"jobName\", js.job_status \"jobStatus\", "
        + "js.job_end_time \"jobEndTime\", je.event_name \"eventName\", je.event_details \"eventDetails\", "
        + "je.created_date \"eventDate\" from ggds_invdb.job_list jl "
        + "left join ggds_invdb.job_status js on jl.job_id = js.job_id "
        + "left join ggds_invdb.job_event je on js.job_status_id = je.job_status_id "
        + "where jl.job_name = :jobName and js.layer_id = :layerId "
        + "and js.reporting_year = :rptYr order by je.created_date desc limit 1", resultSetMapping = "job_event_dto")
@SqlResultSetMapping(name = "job_event_dto", classes = @ConstructorResult(targetClass = JobEventDto.class, columns = {
        @ColumnResult(name = "jobName", type = String.class),
        @ColumnResult(name = "jobStatus", type = String.class),
        @ColumnResult(name = "jobEndTime", type = Date.class),
        @ColumnResult(name = "eventName", type = String.class),
        @ColumnResult(name = "eventDetails", type = String.class),
        @ColumnResult(name = "eventDate", type = Date.class)
}))
public class JobEvent implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "job_event_id")
    private Long jobEventId;

    @Column(name = "job_status_id")
    private Long jobStatusId;

    @ManyToOne
    @JoinColumn(name = "job_status_id", insertable = false, updatable = false)
    private JobStatus jobStatus;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "event_details")
    private String eventDetails;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdDate;
}
