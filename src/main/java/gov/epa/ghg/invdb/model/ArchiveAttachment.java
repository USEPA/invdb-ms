package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "archive_attachment")
public class ArchiveAttachment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long attachmentId;

    @Column(name = "attachment_content")
    private byte[] attachmentContent;

    @Column(name = "attachment_name")
    private String attachmentName;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdDate;

    @Column(name = "CREATED_BY")
    private Integer createdBy;

    @OneToOne(mappedBy = "archiveAttachment")
    private ArchivePackage archivePackage;
}
