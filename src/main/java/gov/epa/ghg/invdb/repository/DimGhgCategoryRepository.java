package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimGhgCategory;

@Repository
public interface DimGhgCategoryRepository extends BaseRepository<DimGhgCategory, Integer> {
    List<DimGhgCategory> findByOrderByGhgCategoryNameAsc();
}
