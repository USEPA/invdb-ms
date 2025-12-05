package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimSubsector;

@Repository
public interface DimSubsectorRepository extends BaseDimTableRepository<DimSubsector, Integer> {
    @Query("SELECT s FROM DimSubsector s WHERE s.subsectorId IN ("
            + "SELECT MAX(si.subsectorId) FROM DimSubsector si GROUP BY si.subsectorName) "
            + "ORDER BY s.subsectorName ASC")
    List<DimSubsector> findAllOrderedBySubsectorName();

    @Override
    @Query(value = "select ggds_invdb.handle_dimfield_value_change('dim_subsector', 'subsector_name', 'subsector', :currentValue, :newValue, 'subssector_id', :userId)", nativeQuery = true)
    void handleDimfieldValueChange(String currentValue, String newValue, Integer userId);
}
