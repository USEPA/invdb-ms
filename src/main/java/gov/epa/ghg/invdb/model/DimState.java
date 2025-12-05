package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_state")
public class DimState implements Serializable {

    @Id
    @Column(name = "state_id")
    private Integer stateId;

    @Column(name = "state")
    private String stateCode;

    @OneToMany(mappedBy = "reportOutputState")
    private java.util.Set<QcCompReportOutput> reportOutputs;
}
