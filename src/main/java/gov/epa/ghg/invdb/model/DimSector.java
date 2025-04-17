package gov.epa.ghg.invdb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_sector")
@SequenceGenerator(name = "sector_seq_gen", sequenceName = "dim_sector_sector_id_seq", allocationSize = 1)
public class DimSector implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sector_seq_gen")
    @Column(name = "sector_id")
    private Integer sectorId;

    @Column(name = "sector_code")
    private String sectorCode;

    @Column(name = "sector_name")
    private String sectorName;

    @OneToMany(mappedBy = "sector")
    private java.util.Set<DimSourceName> sourceNames;

    @OneToMany(mappedBy = "sector")
    private java.util.Set<DimSubsector> subSectors;
}
