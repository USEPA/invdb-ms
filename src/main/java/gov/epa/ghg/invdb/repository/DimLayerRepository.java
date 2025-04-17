package gov.epa.ghg.invdb.repository;

import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimLayer;
import gov.epa.ghg.invdb.rest.dto.DimLayerDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface DimLayerRepository extends JpaRepository<DimLayer, Long> {
    @Query(name = "get_layers", nativeQuery = true)
    java.util.List<DimLayerDto> findLayersWithYearId();
}
