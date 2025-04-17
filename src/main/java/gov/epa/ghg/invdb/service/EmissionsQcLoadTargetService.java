package gov.epa.ghg.invdb.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.epa.ghg.invdb.repository.DimEmissionsQcLoadTargetRepository;
import jakarta.transaction.Transactional;

@Service
public class EmissionsQcLoadTargetService {
    @Autowired
    private DimEmissionsQcLoadTargetRepository emissionsQcLoadTargetRepository;

    @Transactional
    public void updateQuery(Integer reportRowId, Integer userId, Integer emissionsQcId) {
        emissionsQcLoadTargetRepository.updateReportRowId(reportRowId, new Date(), userId, emissionsQcId);
    }
}
