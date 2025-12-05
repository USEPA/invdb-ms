package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimGhgCategory;

@Repository
public interface DimGhgCategoryRepository extends BaseDimTableRepository<DimGhgCategory, Integer> {
    @Query("SELECT g FROM DimGhgCategory g WHERE g.ghgCategoryId IN ("
            + "SELECT MAX(gi.ghgCategoryId) FROM DimGhgCategory gi GROUP BY gi.ghgCategoryName) "
            + "ORDER BY g.ghgCategoryName ASC")
    List<DimGhgCategory> findAllOrderedByCategoryName();

    @Override
    @Query(value = "select ggds_invdb.handle_dimfield_value_change('dim_ghg_category', 'ghg_category_name', 'ghg_category_name', :currentValue, :newValue, 'ghg_category_id', :userId)", nativeQuery = true)
    void handleDimfieldValueChange(String currentValue, String newValue, Integer userId);
}
