package gov.epa.ghg.invdb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_ghg_category")
@SequenceGenerator(name = "ghgcategory_seq_gen", sequenceName = "dim_ghg_category_ghg_category_id_seq", allocationSize = 1)
public class DimGhgCategory implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ghgcategory_seq_gen")
    @Column(name = "ghg_category_id")
    private Integer ghgCategoryId;

    @Column(name = "ghg_category_code")
    private String ghgCategoryCode;

    @Column(name = "ghg_category_name")
    private String ghgCategoryName;
}
