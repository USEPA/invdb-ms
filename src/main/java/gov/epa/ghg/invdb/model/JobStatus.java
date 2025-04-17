package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "job_status")
public class JobStatus implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "job_status_id")
    private Long jobStatusId;

    @Column(name = "job_id")
    private Integer jobId;

    @ManyToOne
    @JoinColumn(name = "job_id", insertable = false, updatable = false)
    private JobList job;

    @Column(name = "reporting_year")
    private Integer reportingYear;

    @Column(name = "layer_id")
    private Integer layerId;

    @Column(name = "job_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date jobStartTime;

    @Column(name = "job_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date jobEndTime;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdDate;

    @Column(name = "CREATED_BY")
    private Integer createdBy;

    @Column(name = "LAST_UPDATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date lastUpdatedDate;

    @Column(name = "LAST_UPDATED_BY")
    private Integer lastUpdatedBy;

    @OneToMany(mappedBy = "jobStatus")
    private java.util.Set<JobEvent> jobEvents;
}
