package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimReportRow;
import gov.epa.ghg.invdb.rest.dto.DimReportRowDto;

@Repository
public interface DimReportRowRepository extends JpaRepository<DimReportRow, Long> {

        @Query("select new gov.epa.ghg.invdb.rest.dto.DimReportRowDto(drr.reportRowId, drr.reportId, drr.rowOrder, "
                        + "drr.rowGroup, drr.rowSubgroup, drr.rowTitle, drr.totalsFlag, drr.excludeFlag, "
                        + "drr.queryFormulaId, qf.formulaPrefix, drr.queryFormulaParameters) "
                        + "from DimReportRow drr "
                        + "left join drr.queryFormula qf "
                        + "where reportId = :reportId "
                        + "order by drr.rowOrder")
        List<DimReportRowDto> findByReportId(Long reportId);

        @Query("select new gov.epa.ghg.invdb.rest.dto.DimReportRowDto(drr.reportRowId, drr.reportId, drr.rowOrder, "
                        + "drr.rowGroup, drr.rowSubgroup, drr.rowTitle, drr.totalsFlag, drr.excludeFlag, "
                        + "drr.queryFormulaId, qf.formulaPrefix, drr.queryFormulaParameters) "
                        + "from DimReportRow drr "
                        + "left join drr.queryFormula qf "
                        + "where reportId = :reportId and (TRIM(drr.totalsFlag) = '' OR drr.totalsFlag IS NULL) "
                        + "order by drr.rowOrder")
        List<DimReportRowDto> findNonTotalsByReportId(Long reportId);

}
