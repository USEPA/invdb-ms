package gov.epa.ghg.invdb.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.epa.ghg.invdb.repository.BaseRepository;
import gov.epa.ghg.invdb.repository.DimCategoryRepository;
import gov.epa.ghg.invdb.repository.DimFuelTypeRepository;
import gov.epa.ghg.invdb.repository.DimGhgCategoryRepository;
import gov.epa.ghg.invdb.repository.DimGhgRepository;
import gov.epa.ghg.invdb.repository.DimSectorRepository;
import gov.epa.ghg.invdb.repository.DimSubsectorRepository;
import jakarta.annotation.PostConstruct;

@Component
public class RepositoryFactory {

    @Autowired
    private DimSectorRepository dimSectorRepository;
    @Autowired
    private DimSubsectorRepository dimSubsectorRepository;
    @Autowired
    private DimCategoryRepository dimCategoryRepository;
    @Autowired
    private DimFuelTypeRepository dimFuelTypeRepository;
    @Autowired
    private DimGhgCategoryRepository dimGhgCategoryRepository;
    @Autowired
    private DimGhgRepository dimGhgRepository;

    private final Map<String, BaseRepository<?, ?>> repositoryMap = new HashMap<>();

    @PostConstruct
    public void init() {
        repositoryMap.put("DIM_SECTOR", dimSectorRepository);
        repositoryMap.put("DIM_SUBSECTOR", dimSubsectorRepository);
        repositoryMap.put("DIM_CATEGORY", dimCategoryRepository);
        repositoryMap.put("DIM_FUEL_TYPE", dimFuelTypeRepository);
        repositoryMap.put("DIM_GHG_CATEGORY", dimGhgCategoryRepository);
        repositoryMap.put("DIM_GHG", dimGhgRepository);
        // Add other repositories to the map as needed
    }

    @SuppressWarnings("unchecked")
    public <T, ID extends Serializable> BaseRepository<T, ID> getRepository(String tablename) {
        BaseRepository<T, ID> repository = (BaseRepository<T, ID>) repositoryMap.get(tablename);
        if (repository == null) {
            throw new IllegalArgumentException("Unknown table name: " + tablename);
        }
        return repository;
    }
}
