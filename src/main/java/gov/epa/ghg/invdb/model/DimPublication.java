package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_publication")
public class DimPublication implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "publication_id")
    private Integer id;

    @Column(name = "row_name")
    private String rowName;

    @Column(name = "row_prefix")
    private String rowPrefix;

    @Column(name = "prepare_button_text")
    private String prepareButtonText;

    @Column(name = "prepare_button_script")
    private String prepareButtonScript;

    @Column(name = "refine_button_text")
    private String refineButtonText;

    @Column(name = "refine_button_script")
    private String refineButtonScript;

    @Column(name = "layer_id")
    private Integer layerId;

    @OneToMany(mappedBy = "dimPublication")
    private java.util.Set<PublicationObject> publicationObjects;
}
