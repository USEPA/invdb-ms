package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "dashboard_component_status")
public class DashboardComponentStatus implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "component_status_id")
    private Long componentStatusId;

    @Column(name = "pub_year_id")
    private Integer pubYearId;

    @Column(name = "layer_id")
    private Integer layerId;

    @Column(name = "component_id")
    private Integer componentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "component_id", insertable = false, updatable = false)
    private DashboardComponent dashboardComponent;

    @Column(name = "component_status")
    private String componentStatus;

    @Column(name = "last_chronjob_rundate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChronjobRundate;

    @Column(name = "last_chronjob_runstatus")
    private String lastChronjobRunstatus;
}
