package gov.epa.ghg.invdb.repository;

import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimPublicationYear;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface DimPublicationYearRepository extends JpaRepository<DimPublicationYear, Long> {
    @Query(value = "select ggds_invdb.invoke_target_year_initialization(:userId, :targetYear, :sourceYear)", nativeQuery = true)
    String invokeTargetYearInit(Integer userId, Integer targetYear, Integer sourceYear);
}
