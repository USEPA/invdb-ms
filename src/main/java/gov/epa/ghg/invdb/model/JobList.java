package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "job_list")
public class JobList implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "job_id")
    private Integer jobId;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "job_description")
    private String jobDescription;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdDate;

    @Column(name = "CREATED_BY")
    private Integer createdBy;

    @OneToMany(mappedBy = "job")
    private java.util.Set<JobStatus> jobs;
}
