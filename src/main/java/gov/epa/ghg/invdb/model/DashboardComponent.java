package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dashboard_component")
public class DashboardComponent implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "component_id")
    private Integer componentId;

    @Column(name = "component_name")
    private String componentName;

    @Column(name = "component_label")
    private String componentLabel;

    @Column(name = "parent_component_id")
    private Integer parentComponentId;

    @OneToMany(mappedBy = "dashboardComponent")
    private java.util.Set<DashboardComponentStatus> componentStatusSet;
}
