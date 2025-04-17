package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "validation_log_extract")
public class ValidationLogExtract implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "attachment_id")
    private Long attachmentId;

    @Column(name = "emissionsqc_load_target_id")
    private Long emissionsQcLoadTargetId;

    @Column(name = "cell_value")
    private String cellValue;

    @Column(name = "cell_location")
    private String cellLocation;

    @Column(name = "description")
    private String description;

    @Column(name = "error_type")
    private Integer errorType;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdDate;

    @Column(name = "created_user_id")
    private Integer createdBy;
}
