package gov.epa.ghg.invdb.util;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.epa.ghg.invdb.model.BaseModel;
import gov.epa.ghg.invdb.model.DimCategory;
import gov.epa.ghg.invdb.model.DimFuelType;
import gov.epa.ghg.invdb.model.DimGhg;
import gov.epa.ghg.invdb.model.DimGhgCategory;
import gov.epa.ghg.invdb.model.DimSector;
import gov.epa.ghg.invdb.model.DimSubsector;
import gov.epa.ghg.invdb.rest.dto.EntityInfo;
import jakarta.annotation.PostConstruct;

@Component
public class EntityFactory {

    @Autowired
    private ModelMapper modelMapper;

    private final Map<String, EntityInfo> entityMap = new HashMap<>();

    @PostConstruct
    public void init() {
        entityMap.put("DIM_SECTOR", new EntityInfo(DimSector.class, "sectorName"));
        entityMap.put("DIM_SUBSECTOR", new EntityInfo(DimSubsector.class, "subsectorName"));
        entityMap.put("DIM_CATEGORY", new EntityInfo(DimCategory.class, "categoryName"));
        entityMap.put("DIM_FUEL_TYPE", new EntityInfo(DimFuelType.class, "fuelTypeName"));
        entityMap.put("DIM_GHG_CATEGORY", new EntityInfo(DimGhgCategory.class, "ghgCategoryName"));
        entityMap.put("DIM_GHG", new EntityInfo(DimGhg.class, "ghgLongname"));
        // Add other repositories to the map as needed
    }

    // Add other repositories as needed
    @SuppressWarnings("unchecked")
    public <T extends BaseModel> T getEntity(String tablename, Map<String, Object> record) {
        Class<? extends BaseModel> entityClass = entityMap.get(tablename).getClazz();
        if (entityClass != null) {
            return (T) modelMapper.map(record, entityClass);
        } else {
            throw new IllegalArgumentException("Invalid table name: " + tablename);
        }
    }

    public String getUniqueFieldValue(String tablename, Map<String, Object> record) {
        String uniqueField = entityMap.get(tablename).getUniqueField();
        return record.get(uniqueField).toString();
    }
}
