package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.ReportOutputEmissionChange;
import gov.epa.ghg.invdb.rest.dto.ReportOutputEmissionChangeDto;

@Repository
public interface ReportOutputEmissionChangeRepository extends JpaRepository<ReportOutputEmissionChange, Long> {

    @Query("select new gov.epa.ghg.invdb.rest.dto.ReportOutputEmissionChangeDto(roec.emissionChangeId, "
            + "roec.reportRowId, roec.startYear, roec.midYear, endYear, startToMidAbsolute, startToMidPercent, "
            + "startToEndAbsolute, startToEndPercent, midToEndAbsolute, midToEndPercent, prevToEndAbsolute, "
            + "prevToEndPercent, startToMidOfTotalPercent, startToEndOfTotalPercent, midToEndOfTotalPercent, "
            + "prevToEndOfTotalPercent, startToMidOfGasPercent, startToEndOfGasPercent, midToEndOfGasPercent, "
            + "prevToEndOfGasPercent, endYrGasPercent, endYrAllEmissionsPercent) "
            + "from ReportOutputEmissionChange roec "
            + "left join roec.reportRow reportRow "
            + "where reportRow.reportId = :reportId ")
    List<ReportOutputEmissionChangeDto> findByReportId(Long reportId);
}
