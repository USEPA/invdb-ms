package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimExcelReport;
import gov.epa.ghg.invdb.rest.dto.DimExcelReportDto;

@Repository
public interface DimExcelReportRepository extends JpaRepository<DimExcelReport, Integer> {
    @Query("select new gov.epa.ghg.invdb.rest.dto.DimExcelReportDto(excelReportId, "
            + "reportName, filename, fileSize, fileType) "
            + "from DimExcelReport where layerId = :layerId and reportingYear = :rptYr "
            + " order by excelReportId")
    List<DimExcelReportDto> getExcelReports(@Param("layerId") Integer layerId,
            @Param("rptYr") Integer reportingYear);
}
