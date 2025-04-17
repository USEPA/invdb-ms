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
import jakarta.annotation.PostConstruct;

@Component
public class EntityFactory {

    @Autowired
    private ModelMapper modelMapper;

    private final Map<String, Class<? extends BaseModel>> entityMap = new HashMap<>();

    @PostConstruct
    public void init() {
        entityMap.put("DIM_SECTOR", DimSector.class);
        entityMap.put("DIM_SUBSECTOR", DimSubsector.class);
        entityMap.put("DIM_CATEGORY", DimCategory.class);
        entityMap.put("DIM_FUEL_TYPE", DimFuelType.class);
        entityMap.put("DIM_GHG_CATEGORY", DimGhgCategory.class);
        entityMap.put("DIM_GHG", DimGhg.class);
        // Add other repositories to the map as needed
    }

    // Add other repositories as needed
    @SuppressWarnings("unchecked")
    public <T extends BaseModel> T getEntity(String tablename, Map<String, Object> record) {
        Class<? extends BaseModel> entityClass = entityMap.get(tablename);
        if (entityClass != null) {
            return (T) modelMapper.map(record, entityClass);
        } else {
            throw new IllegalArgumentException("Invalid table name: " + tablename);
        }
    }
}
