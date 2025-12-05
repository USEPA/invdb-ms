package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimSector;

@Repository
public interface DimSectorRepository extends BaseDimTableRepository<DimSector, Integer> {
    @Query("SELECT s FROM DimSector s WHERE s.sectorId IN ("
            + "SELECT MAX(si.sectorId) FROM DimSector si GROUP BY si.sectorName) "
            + "ORDER BY s.sectorName ASC")
    List<DimSector> findAllOrderedBySectorName();

    @Override
    @Query(value = "select ggds_invdb.handle_dimfield_value_change('dim_sector', 'sector_name', 'sector', :currentValue, :newValue, 'sector_id', :userId)", nativeQuery = true)
    void handleDimfieldValueChange(String currentValue, String newValue, Integer userId);

	DimSector findBySectorId(Long long1);
}
