package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "archive_object")
public class ArchiveObject implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_object_id")
    private Long objectId;

    @Column(name = "archive_package_id")
    private Integer packageId;

    @Column(name = "object_name")
    private String objectName;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "srcfile_attachment_id")
    private Long srcfileAttachmentId;

    @Column(name = "last_updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date lastUpdatedDate;

    @Column(name = "last_updated_by")
    private Integer lastUpdatedBy;
}
