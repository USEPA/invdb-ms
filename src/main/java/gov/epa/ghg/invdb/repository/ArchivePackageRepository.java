package gov.epa.ghg.invdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.ArchivePackage;
import gov.epa.ghg.invdb.rest.dto.ArchivePackageRetrieveDto;

@Repository
public interface ArchivePackageRepository extends JpaRepository<ArchivePackage, Integer> {
        @Query(name = "get_archive_packages_summary", nativeQuery = true)
        java.util.List<ArchivePackageRetrieveDto> getPackagesByLayerAndYear(
                        Integer layer, Integer year);
}
