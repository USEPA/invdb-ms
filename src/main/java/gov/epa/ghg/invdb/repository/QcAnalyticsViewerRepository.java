package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.QcAnalyticsViewer;
import gov.epa.ghg.invdb.rest.dto.QcAnalyticsViewerDto;

@Repository
public interface QcAnalyticsViewerRepository extends JpaRepository<QcAnalyticsViewer, Long> {
    @Query(value = "select ggds_invdb.generate_current_dataset_as_landscape_json(:yearId, :layerId, :userId) ", nativeQuery = true)
    String getCurrentDatasetJson(Integer yearId, Integer layerId, Integer userId);

    @Query("select new gov.epa.ghg.invdb.rest.dto.QcAnalyticsViewerDto( "
            + "recalcJobStatus, outlierJobStatus, viewerId) from QcAnalyticsViewer")
    List<QcAnalyticsViewerDto> getViewerStatuses();
}
