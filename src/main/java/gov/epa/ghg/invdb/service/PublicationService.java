package gov.epa.ghg.invdb.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import gov.epa.ghg.invdb.repository.PublicationObjectRepository;
import gov.epa.ghg.invdb.util.ExcelUtil;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PublicationService {
    @Autowired
    private PublicationObjectRepository pubObjRepository;
    @Autowired
    private ExcelUtil excelUtil;
    private SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");

    @Transactional
    public void save(MultipartFile file, long pubObjectId, String tablename, int userId)
            throws Exception {
        Date today = new Date();
        String tableName_Date = tablename.concat("_").concat(sdf.format(today));
        Map<String, String> resultMap = excelUtil.convertToJson(file, "Export");
        pubObjRepository.updateRawDataInfo(resultMap.get("jsonAsString"), tableName_Date,
                Integer.parseInt(resultMap.get("noOfRecords")), today, userId, pubObjectId);
    }
}
