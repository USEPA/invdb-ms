package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimQcCompReportRow;
import gov.epa.ghg.invdb.rest.dto.DimQcCompReportRowDto;

@Repository
public interface DimQcCompReportRowRepository extends JpaRepository<DimQcCompReportRow, Long> {

    @Query("select new gov.epa.ghg.invdb.rest.dto.DimQcCompReportRowDto(drr.reportRowId, drr.reportId, drr.rowOrder, "
            + "drr.rowGroup, drr.rowSubgroup, drr.rowTitle, drr.totalsFlag, drr.excludeFlag) "
            + "from DimQcCompReportRow drr "
            + "where reportId = :reportId "
            + "order by drr.rowOrder"
            )
    List<DimQcCompReportRowDto> findByReportId(Long reportId);

    @Query("select new gov.epa.ghg.invdb.rest.dto.DimQcCompReportRowDto(drr.reportRowId, drr.reportId, drr.rowOrder, "
    + "drr.rowGroup, drr.rowSubgroup, drr.rowTitle, drr.totalsFlag, drr.excludeFlag) "
    + "from DimQcCompReportRow drr "
     + "where reportId = :reportId and TRIM(drr.totalsFlag) = '' OR drr.totalsFlag IS NULL "
    + "order by drr.rowOrder")
    List<DimQcCompReportRowDto> findNonTotalsByReportId(Long reportId);


}