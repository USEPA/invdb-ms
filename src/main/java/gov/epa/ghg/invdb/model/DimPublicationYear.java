package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_publication_year")
public class DimPublicationYear implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pub_year_id")
    private Integer id;
    @Column(name = "pub_year")
    private Integer year;
    @Column(name = "gwp_column")
    private String gwpColumn;
    @Column(name = "max_time_series")
    private Integer maxTimeSeries;
    @OneToMany(mappedBy = "pubYear")
    private java.util.Set<DimSourceName> sourceNames;
}
