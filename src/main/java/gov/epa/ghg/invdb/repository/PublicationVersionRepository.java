package gov.epa.ghg.invdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.PublicationVersion;

@Repository
public interface PublicationVersionRepository extends JpaRepository<PublicationVersion, Long> {
        java.util.List<PublicationVersion> findByLayerIdAndPubYear(Integer layerId, Integer pubYear);

        @Query(value = "select ggds_invdb.publication_create_version(:yearId, :layerId, :version, :userId)", nativeQuery = true)
        Long createVersion(Integer yearId, Integer layerId, String version, Integer userId);
}
