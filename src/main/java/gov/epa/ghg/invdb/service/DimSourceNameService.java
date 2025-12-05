package gov.epa.ghg.invdb.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import gov.epa.ghg.invdb.model.DimCategory;
import gov.epa.ghg.invdb.model.DimPublicationYear;
import gov.epa.ghg.invdb.model.DimSector;
import gov.epa.ghg.invdb.model.DimSourceName;
import gov.epa.ghg.invdb.repository.DimCategoryRepository;
import gov.epa.ghg.invdb.repository.DimPublicationYearRepository;
import gov.epa.ghg.invdb.repository.DimSectorRepository;
import gov.epa.ghg.invdb.repository.DimSourceNameRepository;
import gov.epa.ghg.invdb.repository.DimSourceNameRepository.PubDimSourceWithActiveCat;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class DimSourceNameService {
	
    @Autowired
    private DimSourceNameRepository dimSourceNameRepository;
    
    @Autowired
    private DimPublicationYearRepository dimPublicationYearRepository;
    
    @Autowired
    private DimCategoryRepository dimCategoryRepository;
    
    @Autowired
    private DimSectorRepository dimSectorRepository;
    
    public List<PubDimSourceWithActiveCat> getFilteredDimSource(Integer dimLayerId, Integer dimYearId) {
    	return dimSourceNameRepository.getDimSourceWithActiveCategory(dimYearId, dimLayerId);
    }
	
    @Transactional
    public boolean saveDimSourceName(Map<String, Object> dimSourceObj) throws DataIntegrityViolationException, Exception {
    	boolean success = false;
    	if (dimSourceObj.get("id") != null) {
    		DimSourceName dsn = dimSourceNameRepository.findById(((Integer)dimSourceObj.get("id")).longValue()).orElse(null);
    		//create new
    		if (dsn == null) {
    			dsn = new DimSourceName();
    			//JPA uses sequence even if you set the Id, it is used just to display a number on the UI 
    			dsn.setId((Integer)dimSourceObj.get("id"));
    		}
			dsn.setName(dimSourceObj.get("name").toString());
			DimCategory dCategory = dimCategoryRepository.findByCategoryId(((Integer)dimSourceObj.get("categoryId")).longValue());
			dsn.setDimCategory(dCategory);
			DimSector dSector = dimSectorRepository.findBySectorId(((Integer)dimSourceObj.get("sectorId")).longValue());
			dsn.setSector(dSector);
			if (dsn.getLayerId() == null && ((Integer)dimSourceObj.get("layerId")) != null) {
				dsn.setLayerId((Integer)dimSourceObj.get("layerId"));
			}
			if (dsn.getPubYear() == null && ((Integer)dimSourceObj.get("pubYearId")) != null) {
				DimPublicationYear dpy  = dimPublicationYearRepository.findById(((Integer)dimSourceObj.get("pubYearId")).longValue()).orElse(null);
				dsn.setPubYear(dpy);
			}
			if(dimSourceObj.get("subCategory1") != null) {
				dsn.setSubCategory1(dimSourceObj.get("subCategory1").toString());
			}
			dimSourceNameRepository.saveAndFlush(dsn);
			success = true;
    	}
    	return success;
    }

    public <T, ID extends Serializable> boolean deleteRecord(Map<String, Object> recordMap) 
    		throws DataIntegrityViolationException, Exception{
		dimSourceNameRepository.deleteById(((Integer)recordMap.get("id")).longValue());
		return true;
    }
    
    public Long findMaxIdOfDataSourceName() {
    	return dimSourceNameRepository.findMaxIdNative().orElse(null);
    }

}
