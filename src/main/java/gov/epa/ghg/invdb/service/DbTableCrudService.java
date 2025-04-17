package gov.epa.ghg.invdb.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import gov.epa.ghg.invdb.repository.BaseRepository;
import gov.epa.ghg.invdb.repository.DimCategoryRepository;
import gov.epa.ghg.invdb.repository.DimFuelTypeRepository;
import gov.epa.ghg.invdb.repository.DimGhgCategoryRepository;
import gov.epa.ghg.invdb.repository.DimGhgRepository;
import gov.epa.ghg.invdb.repository.DimSectorRepository;
import gov.epa.ghg.invdb.repository.DimSubsectorRepository;
import gov.epa.ghg.invdb.rest.dto.DimCategoryDto;
import gov.epa.ghg.invdb.rest.dto.DimFuelTypeDto;
import gov.epa.ghg.invdb.rest.dto.DimGhgCategoryDto;
import gov.epa.ghg.invdb.rest.dto.DimGhgDto;
import gov.epa.ghg.invdb.rest.dto.DimSectorDto;
import gov.epa.ghg.invdb.rest.dto.DimSubsectorDto;
import gov.epa.ghg.invdb.util.EntityFactory;
import gov.epa.ghg.invdb.util.RepositoryFactory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

// Implement CRUD operations for database tables
// This service class can include methods for creating, reading, updating, and deleting records
// from various database tables. You may want to inject necessary repositories and implement
// business logic here.
@Service
@Log4j2
public class DbTableCrudService {

    @Autowired
    private DimSubsectorRepository dimSubsectorRepository;
    @Autowired
    private DimCategoryRepository dimCategoryRepository;
    @Autowired
    private DimSectorRepository dimSectorRepository;
    @Autowired
    private DimFuelTypeRepository dimFuelTypeRepository;
    @Autowired
    private DimGhgCategoryRepository dimGhgCategoryRepository;
    @Autowired
    private DimGhgRepository dimGhgRepository;
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private EntityFactory entityFactory;

    public List<?> getRecords(String tablename, HttpServletResponse response) {
        List<?> results = null;
        switch (tablename) {
            case "DIM_SECTOR":
                results = dimSectorRepository.findByOrderBySectorNameAsc().stream()
                        .map(sector -> new DimSectorDto(sector.getSectorId(), sector.getSectorCode(),
                                sector.getSectorName()))
                        .collect(Collectors.toList());
                break;
            case "DIM_SUBSECTOR":
                results = dimSubsectorRepository.findByOrderBySubsectorNameAsc().stream()
                        .map(subsector -> new DimSubsectorDto(subsector.getSubsectorId(), subsector.getSubsectorCode(),
                                subsector.getSubsectorName(), subsector.getSectorId()))
                        .collect(Collectors.toList());
                break;
            case "DIM_CATEGORY":
                results = dimCategoryRepository.findByOrderByCategoryNameAsc().stream()
                        .map(cat -> new DimCategoryDto(cat.getCategoryId(), cat.getCategoryCode(),
                                cat.getCategoryName(), cat.getSubsectorId()))
                        .collect(Collectors.toList());
                break;
            case "DIM_FUEL_TYPE":
                results = dimFuelTypeRepository.findByOrderByFuelTypeNameAsc().stream()
                        .map(ft -> new DimFuelTypeDto(ft.getFuelTypeId(), ft.getFuelTypeCode(),
                                ft.getFuelTypeName()))
                        .collect(Collectors.toList());
                break;
            case "DIM_GHG_CATEGORY":
                results = dimGhgCategoryRepository.findByOrderByGhgCategoryNameAsc().stream()
                        .map(ghgcat -> new DimGhgCategoryDto(ghgcat.getGhgCategoryId(), ghgcat.getGhgCategoryCode(),
                                ghgcat.getGhgCategoryName()))
                        .collect(Collectors.toList());
                break;
            case "DIM_GHG":
                results = dimGhgRepository.findByOrderByGhgLongnameAsc().stream()
                        .map(ghg -> new DimGhgDto(ghg.getId(), ghg.getGhgCode(),
                                ghg.getGhgLongname(), ghg.getGhgCategoryId(), ghg.getAr4Gwp(), ghg.getAr5Gwp(),
                                ghg.getAr5fGwp(), ghg.getAr6Gwp(), ghg.getGhgFormula(), ghg.getGhgrpGhgId(),
                                ghg.getGhgShortname(), ghg.getCasNo()))
                        .collect(Collectors.toList());
                break;
            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
        return results;
    }

    public <T, ID extends Serializable> boolean saveRecord(Map<String, Object> recordMap, String tablename) {
        try {
            BaseRepository<T, ID> repository = repositoryFactory.getRepository(tablename);
            T entity = entityFactory.getEntity(tablename, recordMap);
            repository.save(entity);
            return true;
        } catch (DataIntegrityViolationException e) {
            // Handle data integrity violation (e.g., unique constraint violation)
            throw new RuntimeException("Data integrity violation: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error occurred while saving record: {}", e.getMessage());
            throw new RuntimeException("Error occurred while saving record: " +
                    e.getMessage(), e);
        }

    }

    public <T, ID extends Serializable> boolean deleteRecord(Map<String, Object> recordMap, String tablename) {
        try {
            BaseRepository<T, ID> repository = repositoryFactory.getRepository(tablename);
            T entity = entityFactory.getEntity(tablename, recordMap);
            repository.delete(entity);
            return true;
        } catch (DataIntegrityViolationException e) {
            log.error("Unable to delete record due to foreign key constraints.");
            throw new RuntimeException(
                    "Unable to delete record due to foreign key constraints. The record may be referenced by other tables or violates referential integrity constraints.",
                    e);
        } catch (Exception e) {
            log.error("Error occurred while deleting record: {}", e.getMessage());
            throw new RuntimeException("Error occurred while deleting record. ", e);
        }
    }
}
