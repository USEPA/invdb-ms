package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimCategory;

@Repository
public interface DimCategoryRepository extends BaseDimTableRepository<DimCategory, Integer> {
    @Query("SELECT c FROM DimCategory c WHERE c.categoryId IN ("
            + "SELECT MAX(ci.categoryId) FROM DimCategory ci GROUP BY ci.categoryName) "
            + "ORDER BY c.categoryName ASC")
    List<DimCategory> findAllOrderedByCategoryName();
    
    //INVDB-697
    @Query("SELECT c FROM DimCategory c join c.subsector s ORDER BY c.categoryName ASC")
    List<DimCategory> findAllCategoriesAndTheirSubsectors();

    @Override
    @Query(value = "select ggds_invdb.handle_dimfield_value_change('dim_category', 'category_name', 'category', :currentValue, :newValue, 'category_id', :userId)", nativeQuery = true)
    void handleDimfieldValueChange(String currentValue, String newValue, Integer userId);

	DimCategory findByCategoryId(Long long1);
}
