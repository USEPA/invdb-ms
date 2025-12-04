package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimGhg;
import gov.epa.ghg.invdb.rest.dto.ReportOutputGwpCompDto;

@Repository
public interface DimGhgRepository extends BaseDimTableRepository<DimGhg, Integer> {
        @Query(name = "get_ghg_comparison_dets", nativeQuery = true)
        List<ReportOutputGwpCompDto> findGhgComparisonDetails(@Param("reportId") Long reportId);

        @Query("SELECT g FROM DimGhg g WHERE g.id IN ("
                        + "SELECT MAX(gi.id) FROM DimGhg gi GROUP BY gi.ghgLongname) "
                        + "ORDER BY g.ghgLongname ASC")
        List<DimGhg> findAllOrderedByDimGhgLongname();

        @Override
        @Query(value = "select ggds_invdb.handle_dimfield_value_change('dim_ghg', 'ghg_longname', 'ghg_longname', :currentValue, :newValue, 'ghg_id', :userId)", nativeQuery = true)
        void handleDimfieldValueChange(String currentValue, String newValue, Integer userId);
}
