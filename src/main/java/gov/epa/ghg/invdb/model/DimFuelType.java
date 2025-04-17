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
@Table(name = "dim_fuel_type")
@SequenceGenerator(name = "fueltype_seq_gen", sequenceName = "dim_fuel_type_fuel_type_id_seq", allocationSize = 1)
public class DimFuelType implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fueltype_seq_gen")
    @Column(name = "fuel_type_id")
    private Integer fuelTypeId;

    @Column(name = "fuel_type_code")
    private String fuelTypeCode;

    @Column(name = "fuel_type_name")
    private String fuelTypeName;
}
