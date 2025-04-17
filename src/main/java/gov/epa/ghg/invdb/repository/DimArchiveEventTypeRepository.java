package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimArchiveEventType;
import gov.epa.ghg.invdb.rest.dto.ArchiveEventDto;

@Repository
public interface DimArchiveEventTypeRepository extends JpaRepository<DimArchiveEventType, Long> {
    @Query(name = "get_archive_events", nativeQuery = true)
    List<ArchiveEventDto> getArchiveEvents(Integer layer, Integer year);
}