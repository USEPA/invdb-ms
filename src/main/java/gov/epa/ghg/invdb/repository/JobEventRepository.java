package gov.epa.ghg.invdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.JobEvent;
import gov.epa.ghg.invdb.rest.dto.JobEventDto;

@Repository
public interface JobEventRepository extends JpaRepository<JobEvent, Long> {
        @Query(name = "get_job_events", nativeQuery = true)
        JobEventDto getEventDetails(
                        @Param("jobName") String jobName,
                        @Param("layerId") Integer layerId,
                        @Param("rptYr") Integer reportingYear);
}
