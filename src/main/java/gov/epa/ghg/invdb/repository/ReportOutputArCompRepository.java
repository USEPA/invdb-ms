package gov.epa.ghg.invdb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.ReportOutputArComp;
import gov.epa.ghg.invdb.rest.dto.ReportOutputArCompDto;

@Repository
public interface ReportOutputArCompRepository extends JpaRepository<ReportOutputArComp, Long> {
        @Query(name = "get_ar_comp_dets_refresh", nativeQuery = true)
        List<ReportOutputArCompDto> getReportOutputArCompData(@Param("response") String response,
                        @Param("gwpColumn") String arOption, @Param("userId") int userId);

        @Query(name = "get_ar_comp_dets", nativeQuery = true)
        List<ReportOutputArCompDto> getReportOutputArCompData(@Param("reportId") Long reportId,
                        @Param("gwpColumn") String arOption);

        @Query(value = "SELECT CASE WHEN t1.last_updated_date > dr.report_refresh_date THEN true ELSE false END AS latest_ar_comp "
                        + "from ggds_invdb.report_output_ar_comp t1 "
                        + "JOIN ggds_invdb.dim_report_row drr on t1.report_row_id = drr.report_row_id "
                        + "join ggds_invdb.dim_report dr on drr.report_id = dr.report_id "
                        + "where dr.report_id = :reportId and t1.gwp_column = :gwpColumn limit 1", nativeQuery = true)
        Optional<Boolean> findArComparisonDataLatest(@Param("reportId") Long reportId, @Param("gwpColumn") String arOption);

}
