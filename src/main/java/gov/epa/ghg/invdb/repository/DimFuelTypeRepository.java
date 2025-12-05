package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimFuelType;

@Repository
public interface DimFuelTypeRepository extends BaseDimTableRepository<DimFuelType, Integer> {
    @Query("SELECT f FROM DimFuelType f WHERE f.fuelTypeId IN ("
            + "SELECT MAX(fi.fuelTypeId) FROM DimFuelType fi GROUP BY fi.fuelTypeName) "
            + "ORDER BY f.fuelTypeName ASC")
    List<DimFuelType> findAllOrderedByFuelTypeName();

    @Override
    @Query(value = "select ggds_invdb.handle_dimfield_value_change('dim_fuel_type', 'fuel_type_name', 'fuel1', :currentValue, :newValue, 'fuel_type_id', :userId); "
            + "select ggds_invdb.handle_dimfield_value_change('dim_fuel_type', 'fuel_type_name', 'fuel2', :currentValue, :newValue, 'fuel_type_id', :userId);", nativeQuery = true)
    void handleDimfieldValueChange(String currentValue, String newValue, Integer userId);
}
