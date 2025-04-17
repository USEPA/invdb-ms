package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DashboardComponentStatus;
import gov.epa.ghg.invdb.rest.dto.DashboardComponentDto;

@Repository
public interface DashboardComponentStatusRepostitory extends JpaRepository<DashboardComponentStatus, Long> {
    @Query("select new gov.epa.ghg.invdb.rest.dto.DashboardComponentDto(dc.componentId, "
            + "dc.componentName, dc.componentLabel, dcs.componentStatus, dc.parentComponentId) "
            + "from DashboardComponentStatus dcs "
            + "left join dcs.dashboardComponent dc "
            + "where pubYearId = :pubYearId and layerId = :layerId "
            + "order by dc.componentId")
    public List<DashboardComponentDto> findByPubYearIdAndLayerId(int pubYearId, int layerId);
}
