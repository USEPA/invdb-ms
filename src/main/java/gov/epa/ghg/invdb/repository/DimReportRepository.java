package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimReport;
import gov.epa.ghg.invdb.rest.dto.DimReportDto;
import gov.epa.ghg.invdb.rest.dto.ReportWithTabDetails;

@Repository
public interface DimReportRepository extends JpaRepository<DimReport, Long> {
        @Query("select new gov.epa.ghg.invdb.rest.dto.DimReportDto(reportId, reportName, reportTitle, reportRowsHeader, reportRefreshDate, refreshStatus, reportingYear, layerId) "
                        + "from DimReport where layerId = :layerId and reportingYear = :rptYr "
                        + "order by reportTitle")
        List<DimReportDto> findReportDtos(@Param("layerId") Integer layerId,
                        @Param("rptYr") Integer reportingYear);

        @Query(name = "get_report_with_tab_details", nativeQuery = true)
        List<ReportWithTabDetails> findReportwithTabDetails(@Param("layerId") Integer layerId,
                        @Param("rptYr") Integer reportingYear);
}
