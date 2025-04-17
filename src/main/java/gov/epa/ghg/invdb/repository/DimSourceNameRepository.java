package gov.epa.ghg.invdb.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import gov.epa.ghg.invdb.rest.dto.DimSourceNameDto;
import gov.epa.ghg.invdb.model.DimSourceName;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface DimSourceNameRepository extends JpaRepository<DimSourceName, Long> {
    java.util.List<DimSourceNameDto> findByLayerIdAndPubYearYear(Integer layerId, Integer year);

    @Query(name = "check_template_3_dim_source_name", nativeQuery = true)
    java.util.List<DimSourceNameDto> checkTemplate3DimSourceName(
            @Param("sector") String sector,
            @Param("subsector") String subsector,
            @Param("category") String category,
            @Param("subcategory1") String subcategory1,
            @Param("layerId") Integer layerId,
            @Param("rptYr") Integer reportingYear);
}