package gov.epa.ghg.invdb.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimEmissionsQcLoadTarget;
import gov.epa.ghg.invdb.rest.dto.DimEmissionsQcLoadTargetDto;

@Repository
public interface DimEmissionsQcLoadTargetRepository extends JpaRepository<DimEmissionsQcLoadTarget, Integer> {
    @Query("select new gov.epa.ghg.invdb.rest.dto.DimEmissionsQcLoadTargetDto(emissionsQcLoadTargetId, "
            + "eqc.sourceNameId, dimSourceName.name, eqc.reportingYear, eqc.targetTab, "
            + "eqc.rowTitleCell, eqc.anticipatedRowTitle, eqc.dataRef1990, eqc.emissionParameters, "
            + "eqc.reportRowId, rr.reportId, rr.rowGroup, rr.rowSubgroup, rr.rowTitle) "
            + "from DimEmissionsQcLoadTarget eqc "
            + "left join eqc.dimSourceName dimSourceName "
            + "left join eqc.reportRow rr "
            + "where eqc.layerId = :layer and eqc.reportingYear = :year "
            + "order by eqc.emissionsQcLoadTargetId")
    List<DimEmissionsQcLoadTargetDto> getbyLayerAndYear(Integer layer, Integer year);

    @Modifying
    @Query(value = "update ggds_invdb.dim_emissionsqc_load_target "
            + "set report_row_id=:reportRowId, "
            + "last_updated_date=:updatedDate, last_updated_by = :updatedBy "
            + "where emissionsqc_load_target_id=:id", nativeQuery = true)
    void updateReportRowId(@Param("reportRowId") Integer reportRowId,
            @Param("updatedDate") Date updatedDate, @Param("updatedBy") Integer userId,
            @Param("id") Integer emissionsQcId);
}
