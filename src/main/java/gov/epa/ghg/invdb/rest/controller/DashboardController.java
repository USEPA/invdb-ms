package gov.epa.ghg.invdb.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.epa.ghg.invdb.repository.DashboardComponentStatusRepostitory;
import gov.epa.ghg.invdb.rest.dto.DashboardComponentDto;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @Autowired
    private DashboardComponentStatusRepostitory dashboardCompStatusRepository;

    @GetMapping("/overview")
    public List<DashboardComponentDto> loadDashboardOverview(@RequestParam(name = "layerId") int layerId,
            @RequestParam(name = "pubYearId") int pubYearId)
            throws Exception {
        List<DashboardComponentDto> packages = dashboardCompStatusRepository.findByPubYearIdAndLayerId(pubYearId,
                layerId);
        return packages;
    }

}
