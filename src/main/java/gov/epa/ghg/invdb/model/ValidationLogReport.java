package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "validation_log_report")
public class ValidationLogReport implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "tab_name")
    private String tabName;

    @Column(name = "value")
    private String value;

    @Column(name = "row_num")
    private Integer rowNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdDate;

    @Column(name = "created_by")
    private Integer createdBy;
}
