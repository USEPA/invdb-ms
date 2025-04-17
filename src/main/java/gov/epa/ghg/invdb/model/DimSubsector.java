package gov.epa.ghg.invdb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_subsector")
@SequenceGenerator(name = "subsector_seq_gen", sequenceName = "dim_subsector_subsector_id_seq", allocationSize = 1)
public class DimSubsector implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subsector_seq_gen")
    @Column(name = "subsector_id")
    private Integer subsectorId;

    @Column(name = "subsector_code")
    private String subsectorCode;

    @Column(name = "subsector_name")
    private String subsectorName;

    @Column(name = "sector_id")
    private Integer sectorId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sector_id", insertable = false, updatable = false)
    private DimSector sector;

    @OneToMany(mappedBy = "subsector")
    private java.util.Set<DimCategory> categories;
}
