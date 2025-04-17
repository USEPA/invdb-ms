package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimCategory;

@Repository
public interface DimCategoryRepository extends BaseRepository<DimCategory, Integer> {
    List<DimCategory> findByOrderByCategoryNameAsc();
}
