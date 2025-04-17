package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimSector;

@Repository
public interface DimSectorRepository extends BaseRepository<DimSector, Integer> {
    List<DimSector> findByOrderBySectorNameAsc();
}
