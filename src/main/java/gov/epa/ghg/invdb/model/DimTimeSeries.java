package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_time_series")
public class DimTimeSeries implements Serializable {

    @Id
    @Column(name = "year_id")
    private Integer yearId;

    @Column(name = "year")
    private Integer year;

    @OneToMany(mappedBy = "reportOutputYear")
    private java.util.Set<ReportOutput> reportOutputs;
}
