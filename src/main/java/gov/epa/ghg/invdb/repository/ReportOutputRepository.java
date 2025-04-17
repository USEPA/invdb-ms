package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.ReportOutput;
import gov.epa.ghg.invdb.rest.dto.ReportOutputDto;

@Repository
public interface ReportOutputRepository extends JpaRepository<ReportOutput, Long> {

        @Query("select new gov.epa.ghg.invdb.rest.dto.ReportOutputDto(dro.reportOutputId, dro.reportRowId, dro.reportOutputYearId, dro.reportOutputValue) "
                        + "from ReportOutput dro "
                        + "join dro.reportRow reportRow "
                        + "where reportRow.reportId = :reportId ")
        List<ReportOutputDto> findByReportId(Long reportId);

        @Query("select new gov.epa.ghg.invdb.rest.dto.ReportOutputDto(dro.reportOutputId, dro.reportRowId, dro.reportOutputYearId, dro.reportOutputValue) "
                        + "from ReportOutput dro "
                        + "left join dro.reportRow reportRow "
                        + "where reportRow.reportRowId = :reportRowId "
                        + "order by reportRow.reportRowId")
        List<ReportOutputDto> findByReportRowId(Long reportRowId);

        @Query(value = "select ggds_invdb.em_rpt_output_populate(:response, :reportId, :userId)", nativeQuery = true)
        String populateReportOutputTables(@Param("response") String response, @Param("reportId") Long reportId,
                        @Param("userId") int userId);

        @Query(name = "get_rptop_with_percent_dets", nativeQuery = true)
        List<ReportOutputDto> findRptOpWithPercentDets(@Param("reportId") Long reportId);
}
