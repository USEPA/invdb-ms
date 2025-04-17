package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "publication_version")
public class PublicationVersion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pub_version_id")
    private Long pubVersionId;

    @Column(name = "pub_year")
    private Integer pubYear;

    @Column(name = "layer_id")
    private Integer layerId;

    @Column(name = "version_name")
    private String versionName;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "CREATED_BY")
    private Integer createdBy;

    @JsonIgnore
    @OneToMany(mappedBy = "publicationVersion", fetch = FetchType.LAZY)
    private java.util.Set<PublicationObject> publicationObjects;
}
