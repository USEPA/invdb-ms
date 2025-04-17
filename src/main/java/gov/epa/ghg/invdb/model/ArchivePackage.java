package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import java.util.Date;

import gov.epa.ghg.invdb.rest.dto.ArchivePackageRetrieveDto;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "archive_package")
@NamedNativeQuery(name = "get_archive_packages_summary", query = "select archive_package_id, archive_name, archive_description, "
                + "event_name, archive_attachment_id, srcfile_attachement_ids, last_created_date, last_created_by from ggds_invdb.archive_packages_summary(:layer, :year)", resultSetMapping = "ArchivePackageRetrieveDTOMapping")
@SqlResultSetMapping(name = "ArchivePackageRetrieveDTOMapping", classes = {
                @ConstructorResult(targetClass = ArchivePackageRetrieveDto.class, columns = {
                                @ColumnResult(name = "archive_package_id", type = Integer.class),
                                @ColumnResult(name = "archive_name", type = String.class),
                                @ColumnResult(name = "archive_description", type = String.class),
                                @ColumnResult(name = "event_name", type = String.class),
                                @ColumnResult(name = "archive_attachment_id", type = Integer.class),
                                @ColumnResult(name = "srcfile_attachement_ids"),
                                @ColumnResult(name = "last_created_date", type = Date.class),
                                @ColumnResult(name = "last_created_by", type = String.class)
                })
})
public class ArchivePackage implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "archive_package_id")
        private Integer archivePackageId;

        @Column(name = "reporting_year")
        private Integer reportingYear;

        @Column(name = "layer_id")
        private Integer layerId;

        @Column(name = "event_type_id")
        private Integer eventTypeId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "event_type_id", insertable = false, updatable = false)
        private DimArchiveEventType packageEventType;

        @Column(name = "archive_name")
        private String archiveName;

        @Column(name = "archive_description")
        private String archiveDescription;

        @Column(name = "archive_attachment_id")
        private Long archiveAttachmentId;

        @OneToOne(optional = false)
        @JoinColumn(name = "archive_attachment_id", insertable = false, updatable = false)
        private ArchiveAttachment archiveAttachment;

        @Column(name = "LAST_CREATED_DATE")
        @Temporal(TemporalType.TIMESTAMP)
        private java.util.Date lastCreatedDate;

        @Column(name = "LAST_CREATED_BY")
        private Integer lastCreatedBy;

        @ManyToOne(optional = false)
        @JoinColumn(name = "LAST_CREATED_BY", insertable = false, updatable = false)
        private User archivePkgCreateUser;
}
