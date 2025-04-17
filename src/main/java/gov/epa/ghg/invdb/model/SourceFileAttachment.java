package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "source_file_attachment")
public class SourceFileAttachment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long attachmentId;

    @Column(name = "attachment_name")
    private String attachmentName;

    @Column(name = "attachment_type")
    private String attachmentType;

    @Column(name = "attachment_size")
    private Long attachmentSize;

    @Column(name = "content")
    private byte[] content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_file_id", insertable = false, updatable = false)
    private SourceFile sourceFile;

    @Column(name = "source_file_id")
    private Long sourceFileId;

    @Column(name = "last_srcfile_linked_date")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date lastSrcfileLinkedDt;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CREATED_BY", insertable = false, updatable = false)
    private User srcAttachCreateUser;

    @Column(name = "CREATED_BY")
    private Integer createdBy;

    @Column(name = "LAST_UPDATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date lastUpdatedDate;

    @Column(name = "LAST_UPDATED_BY")
    private Integer lastUpdatedBy;

    @Column(name = "HAS_ERROR")
    private boolean hasError;

    @Column(name = "processed_date")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date processedDate;

    @Column(name = "processed_by")
    private Integer processedBy;
}
