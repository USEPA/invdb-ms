package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "report")
public class Report implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "report_id")
        private Long reportId;

        @Column(name = "report_name")
        private String reportName;

        @Column(name = "attachment_name")
        private String attachmentName;

        @Column(name = "attachment_type")
        private String attachmentType;

        @Column(name = "attachment_size")
        private Long attachmentSize;

        @Column(name = "content")
        private byte[] content;

        @Column(name = "processed_date")
        @Temporal(TemporalType.TIMESTAMP)
        private java.util.Date processedDate;

        @Column(name = "PROCESSED_BY")
        private Integer processedBy;

        @Column(name = "reporting_year")
        private Integer reportingYear;

        @Column(name = "layer_id")
        private Integer layerId;

        @Column(name = "report_type")
        private String reportType;

        @Column(name = "HAS_ERROR")
        private boolean hasError;

        @Column(name = "VALIDATION_STATUS")
        private String validationStatus;

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

        @ManyToOne
        @JoinColumn(name = "LAST_UPDATED_BY", insertable = false, updatable = false)
        private User reportLastUpdateUser;

        @Column(name = "last_uploaded_date")
        @Temporal(TemporalType.TIMESTAMP)
        private java.util.Date lastUploadedDate;

        @Column(name = "last_uploaded_by")
        private Integer lastUploadedBy;

        @ManyToOne
        @JoinColumn(name = "last_uploaded_by", insertable = false, updatable = false)
        private User reportLastUploadedUser;

        @ManyToOne
        @JoinColumn(name = "PROCESSED_BY", insertable = false, updatable = false)
        private User reportProcessedByUser;
}
