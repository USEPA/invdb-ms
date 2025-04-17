package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimSubsector;

@Repository
public interface DimSubsectorRepository extends BaseRepository<DimSubsector, Integer> {
    List<DimSubsector> findByOrderBySubsectorNameAsc();
}
