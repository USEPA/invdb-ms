package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimGhg;
import gov.epa.ghg.invdb.rest.dto.ReportOutputGwpCompDto;

@Repository
public interface DimGhgRepository extends BaseRepository<DimGhg, Integer> {
        @Query(name = "get_ghg_comparison_dets", nativeQuery = true)
        List<ReportOutputGwpCompDto> findGhgComparisonDetails(@Param("reportId") Long reportId);

        List<DimGhg> findByOrderByGhgLongnameAsc();
}
