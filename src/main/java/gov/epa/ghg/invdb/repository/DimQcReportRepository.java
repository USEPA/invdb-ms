package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimQcReport;
import gov.epa.ghg.invdb.rest.dto.DimQcReportDto;

@Repository
public interface DimQcReportRepository extends JpaRepository<DimQcReport, Long> {
        @Query("select new gov.epa.ghg.invdb.rest.dto.DimQcReportDto(reportId, reportName, reportTitle, reportRowsHeader, reportRefreshDate, refreshStatus, reportingYear, layerId) "
                + "from DimQcReport where layerId = :layerId and reportingYear = :rptYr "
                + "order by reportTitle")
        List<DimQcReportDto> findReportDtos(@Param("layerId") Integer layerId,
                @Param("rptYr") Integer reportingYear);
}
