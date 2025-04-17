package gov.epa.ghg.invdb.repository;

import org.springframework.stereotype.Repository;
import gov.epa.ghg.invdb.model.DimTimeSeries;
import gov.epa.ghg.invdb.rest.dto.TimeSeriesDto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface DimTimeSeriesRepository extends JpaRepository<DimTimeSeries, Long> {
    @Query("select  new gov.epa.ghg.invdb.rest.dto.TimeSeriesDto(yearId, year) "
            + "from DimTimeSeries dts "
            + "where dts.year <= ("
            + "     select dpy.maxTimeSeries from DimPublicationYear dpy "
            + "     where year = :reportingYr) "
            + " order by dts.yearId")
    List<TimeSeriesDto> findTimeSeriesByReportingYr(Integer reportingYr);
}
