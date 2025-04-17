package gov.epa.ghg.invdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.Report;
import gov.epa.ghg.invdb.rest.dto.ReportDto;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
        @Query("select new gov.epa.ghg.invdb.rest.dto.ReportDto(reportId, "
                        + "reportName, processedDate, CONCAT(processedUserprofile.firstName, ' ', processedUserprofile.lastName), "
                        + "attachmentName, reportingYear, CONCAT(uploadUserprofile.firstName, ' ', uploadUserprofile.lastName), "
                        + "lastUploadedDate, hasError, validationStatus) "
                        + "from Report rpt "
                        + "left join rpt.reportLastUploadedUser uploadUserprofile "
                        + "left join rpt.reportProcessedByUser processedUserprofile "
                        + "where layerId = :layerId and reportingYear = :rptYr order by rpt.lastUploadedDate desc")
        java.util.List<ReportDto> getFilesForDisplayByLayerAndYear(
                        @Param("layerId") Integer layerId,
                        @Param("rptYr") Integer reportingYear);

        @Query("select new gov.epa.ghg.invdb.rest.dto.ReportDto(reportId, reportName) "
                        + "from Report "
                        + "where layerId = :layerId and reportingYear = :rptYr")
        java.util.List<ReportDto> getFilesWithRptNameByLayerAndYear(
                        @Param("layerId") Integer layerId,
                        @Param("rptYr") Integer reportingYear);

        @Modifying
        @Query(value = "update ggds_invdb.report "
                        + "set content= (select file_content from ggds_invdb.dim_excel_report"
                        + " where excel_report_id= :excelReportId) "
                        + "where report_id=:id", nativeQuery = true)
        void updateReportWithExcel(@Param("excelReportId") Integer excelReportId, @Param("id") Long reportId);
}
