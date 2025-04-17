package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "validation_log_load")
public class ValidationLogLoad implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "attachment_id")
    private Long attachmentId;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "field_value")
    private String fieldValue;

    @Column(name = "row_number")
    private Integer rowNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdDate;

    @Column(name = "created_user_id")
    private Integer createdBy;
}
