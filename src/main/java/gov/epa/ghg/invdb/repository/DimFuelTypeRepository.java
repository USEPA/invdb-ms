package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimFuelType;

@Repository
public interface DimFuelTypeRepository extends BaseRepository<DimFuelType, Integer> {
    List<DimFuelType> findByOrderByFuelTypeNameAsc();
}
