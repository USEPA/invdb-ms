package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.ReportOutputKilotons;
import gov.epa.ghg.invdb.rest.dto.ReportOutputDto;

@Repository
public interface ReportOutputKilotonsRepository extends JpaRepository<ReportOutputKilotons, Long> {

        @Query("select new gov.epa.ghg.invdb.rest.dto.ReportOutputDto(dro.reportOutputKilotonsId, dro.reportRowId, dro.reportOutputYearId, dro.reportOutputValue) "
                        + "from ReportOutputKilotons dro "
                        + "left join dro.reportRow reportRow "
                        + "where reportRow.reportId = :reportId ")
        List<ReportOutputDto> findByReportId(Long reportId);

        @Query(value = "select ggds_invdb.em_rpt_output_populate_kilotons(:reportId, :userId)", nativeQuery = true)
        String populateReportOutputKilotonsTables(@Param("reportId") Long reportId,
                        @Param("userId") int userId);
}
