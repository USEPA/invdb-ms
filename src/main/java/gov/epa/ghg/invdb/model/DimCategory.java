package gov.epa.ghg.invdb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_category")
@SequenceGenerator(name = "category_seq_gen", sequenceName = "dim_sector_sector_id_seq", allocationSize = 1)
public class DimCategory implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq_gen")
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_code")
    private String categoryCode;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "subsector_id")
    private Integer subsectorId;
    
    @Column(name = "category_active")
    private String categoryActive;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subsector_id", insertable = false, updatable = false)
    private DimSubsector subsector;
}
