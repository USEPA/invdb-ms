package gov.epa.ghg.invdb.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "qc_analytics_viewer")
public class QcAnalyticsViewer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "viewer_id")
    private Long viewerId;

    @Column(name = "folder_name")
    private String folderName;

    @Column(name = "specifications")
    // @Convert(converter = ParamValueJsonConvertor.class)
    private String specifications;

    @Column(name = "recalc_job_status")
    private String recalcJobStatus;

    @Column(name = "outlier_job_status")
    private String outlierJobStatus;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "CREATED_BY")
    private Integer createdBy;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "CREATED_BY", insertable = false, updatable = false)
    private User viewerCreateUser;
}
