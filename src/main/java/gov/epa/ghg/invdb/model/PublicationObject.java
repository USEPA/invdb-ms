package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "publication_object")
public class PublicationObject implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pub_object_id")
    private Long pubObjectId;

    @Column(name = "pub_version_id")
    private Long pubVersionId;

    @ManyToOne
    @JoinColumn(name = "pub_version_id", insertable = false, updatable = false)
    private PublicationVersion publicationVersion;

    @Column(name = "pub_id")
    private Long pubId;

    @ManyToOne
    @JoinColumn(name = "pub_id", insertable = false, updatable = false)
    private DimPublication dimPublication;

    // @Column(name = "raw_data", columnDefinition = "jsonb")
    // private String rawData;
    @Column(name = "raw_data")
    private String rawData;

    @Column(name = "raw_tablename")
    private String rawTablename;

    @Column(name = "raw_total_records")
    private Integer totalRecordsRaw;

    @Column(name = "last_import_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastImportDate;

    @Column(name = "last_import_by")
    private Integer lastImportBy;

    @ManyToOne
    @JoinColumn(name = "last_import_by", insertable = false, updatable = false)
    private User lastImportUser;

    @Column(name = "refined_data")
    private String refinedData;

    @Column(name = "refined_tablename")
    private String refinedTablename;

    @Column(name = "last_refined_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRefinedDate;

    @Column(name = "last_refined_by")
    private Integer lastRefinedBy;

    @ManyToOne
    @JoinColumn(name = "last_refined_by", insertable = false, updatable = false)
    private User lastRefinedUser;
}
