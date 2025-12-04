package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.QcCompReportOutput;
import gov.epa.ghg.invdb.rest.dto.QcCompReportOutputDto;

@Repository
public interface QcCompReportOutputRepository extends JpaRepository<QcCompReportOutput, Long> {

    @Query("select new gov.epa.ghg.invdb.rest.dto.QcCompReportOutputDto(dro.reportOutputId, dro.reportRowId, dro.reportOutputYearId, dro.reportOutputValue, ros.stateCode) "
            + "from QcCompReportOutput dro "
            + "left join dro.reportRow reportRow "
            + "left join dro.reportOutputState ros "
            + "where reportRow.reportId = :reportId ")
    List<QcCompReportOutputDto> findByReportId(Long reportId);

    // @Query("select new
    // gov.epa.ghg.invdb.rest.dto.QcCompReportOutputDto(dro.reportOutputId,
    // dro.reportRowId, dro.reportOutputYearId, dro.reportOutputValue) "
    // + "from QcCompReportOutput dro "
    // + "left join dro.reportRow reportRow "
    // + "where reportRow.reportRowId = :reportRowId "
    // + "order by reportRow.reportRowId")
    // List<QcCompReportOutputDto> findByReportRowId(Long reportRowId);

    @Query(value = "select ggds_invdb.em_qc_output_populate(:response, :userId)", nativeQuery = true)
    String populateQcQueryResponse(@Param("response") String response, @Param("userId") int userId);
}
