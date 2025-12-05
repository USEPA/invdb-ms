package gov.epa.ghg.invdb.rest.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.epa.ghg.invdb.model.DimPublicationYear;
import gov.epa.ghg.invdb.repository.DimArchiveEventTypeRepository;
import gov.epa.ghg.invdb.repository.DimExcelReportRepository;
import gov.epa.ghg.invdb.repository.DimLayerRepository;
import gov.epa.ghg.invdb.repository.DimPublicationYearRepository;
import gov.epa.ghg.invdb.repository.DimReportRepository;
import gov.epa.ghg.invdb.repository.DimReportRowRepository;
import gov.epa.ghg.invdb.repository.DimSourceNameRepository;
import gov.epa.ghg.invdb.repository.DimSourceNameRepository.PubDimSourceCheck;
import gov.epa.ghg.invdb.repository.DimTimeSeriesRepository;
import gov.epa.ghg.invdb.rest.dto.ArchiveEventDto;
import gov.epa.ghg.invdb.rest.dto.DimExcelReportDto;
import gov.epa.ghg.invdb.rest.dto.DimLayerDto;
import gov.epa.ghg.invdb.rest.dto.DimPublicationYearDto;
import gov.epa.ghg.invdb.rest.dto.DimReportDto;
import gov.epa.ghg.invdb.rest.dto.DimReportRowDto;
import gov.epa.ghg.invdb.rest.dto.DimSourceNameDto;
import gov.epa.ghg.invdb.rest.dto.ReportWithTabDetails;
import gov.epa.ghg.invdb.rest.dto.Tab;
import gov.epa.ghg.invdb.rest.dto.TimeSeriesDto;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/dim")
@Log4j2
public class DimController {
    @Autowired
    private DimLayerRepository dimLayerRepository;
    @Autowired
    private DimPublicationYearRepository dimPubYearRepository;
    @Autowired
    private DimSourceNameRepository dimSourceNameRepository;
    @Autowired
    private DimArchiveEventTypeRepository dimArchiveEventTypeRepository;
    @Autowired
    private DimExcelReportRepository dimExcelReportRepository;
    @Autowired
    private DimReportRepository dimReportRepository;
    @Autowired
    private DimReportRowRepository dimReportRowRepository;
    @Autowired
    private DimTimeSeriesRepository dimTimeSeriesReporsitory;

    @GetMapping("/layers")
    public List<DimLayerDto> getAllLayers() {
        List<DimLayerDto> layers = dimLayerRepository.findLayersWithYearId();
        return layers;
    }

    @GetMapping("/pubYears")
    public List<DimPublicationYearDto> getAllPubYears() {
        List<DimPublicationYear> pubYears = dimPubYearRepository.findAll();
        Collections.sort(pubYears, (p1, p2) -> {
            return p2.getYear() - p1.getYear();
        });
        return pubYears.stream().map(pubYr -> new DimPublicationYearDto(pubYr.getId(),
                pubYr.getYear(), pubYr.getGwpColumn(), pubYr.getMaxTimeSeries())).collect(Collectors.toList());
    }

    @GetMapping("/sourceNames")
    public List<DimSourceNameDto> getSourceNames(@RequestParam(name = "layer") int layerId,
            @RequestParam(name = "year") int rptYear) {
        List<DimSourceNameDto> sourceNames = dimSourceNameRepository.findByLayerIdAndPubYearYear(layerId, rptYear);
        Collections.sort(sourceNames, (p1, p2) -> {
            return p1.getName().compareTo(p2.getName());
        });
        return sourceNames;
    }

    // return true if a dim_source_name is found with provided sector, subsector,
    // category and sub_category_1
    @GetMapping("/checkTemplate3SourceName")
    public Boolean checkTemplate3SourceName(@RequestParam(name = "layerId") int layerId,
            @RequestParam(name = "year") int rptYear,
            @RequestParam(name = "sector") String sector,
            @RequestParam(name = "subsector") String subsector,
            @RequestParam(name = "category") String category,
            @RequestParam(name = "subcategory1") String subcategory1) {
        //List<DimSourceNameDto> sourceNames = dimSourceNameRepository.checkTemplate3DimSourceName(sector, subsector,
                //category, subcategory1, layerId, rptYear);
        List<DimSourceNameDto> sourceNames = dimSourceNameRepository.checkTemplate3DimSourceName(category, subcategory1, layerId, rptYear);
        return sourceNames.size() > 0;
    }
    
    //INVDB-687
    @GetMapping("/checkTemplate3SourceName/{layerId}/{rptYear}/{category}/{subcategory1}")
    public PubDimSourceCheck checkTemplate3SourceName(
    		@PathVariable int layerId,
    		@PathVariable int rptYear,
    		@PathVariable String category,
    		@PathVariable String subcategory1) {
    	//String tempOutput = String.format("the parameters are: layerId:%s, rptYear:%s, category:%s, subcategory1:%s" , layerId, rptYear, category, subcategory1 );
        //System.out.println(tempOutput);
        return dimSourceNameRepository.checkDimSourceName(category, subcategory1, layerId, rptYear);
    }

    @GetMapping("/archiveEventTypes")
    public List<ArchiveEventDto> getArchiveEventTypes(@RequestParam(name = "layer") int layerId,
            @RequestParam(name = "year") int rptYear) {
        List<ArchiveEventDto> eventTypes = dimArchiveEventTypeRepository.getArchiveEvents(layerId, rptYear);
        return eventTypes;
    }

    @GetMapping("/excelReports")
    public List<DimExcelReportDto> getExcelReports(@RequestParam(name = "layer") int layerId,
            @RequestParam(name = "year") int rptYear) {
        List<DimExcelReportDto> excelReports = dimExcelReportRepository.getExcelReports(layerId, rptYear);
        return excelReports;
    }

    @GetMapping("/reports")
    public List<DimReportDto> getReports(@RequestParam(name = "layer") int layerId,
            @RequestParam(name = "year") int rptYear) {
        List<DimReportDto> reportDtos = dimReportRepository.findReportDtos(layerId, rptYear);
        return reportDtos;
    }

    @GetMapping("/reportsWithTabDets")
    public List<DimReportDto> getReportsWithTabDets(@RequestParam(name = "layer") int layerId,
            @RequestParam(name = "year") int rptYear) {
        ObjectMapper mapper = new ObjectMapper();
        List<ReportWithTabDetails> rptWithTabDets = dimReportRepository.findReportwithTabDetails(layerId, rptYear);
        List<DimReportDto> reportDtos = new ArrayList<>();
        for (ReportWithTabDetails report : rptWithTabDets) {
            DimReportDto dto = new DimReportDto();
            dto.setReportId(report.getReportId());
            dto.setReportName(report.getReportName());
            dto.setReportTitle(report.getReportTitle());
            dto.setReportRowsHeader(report.getReportRowsHeader());
            dto.setReportRefreshDate(report.getReportRefreshDate());
            dto.setRefreshStatus(report.getRefreshStatus());
            try {
                List<Tab> tabDetails = mapper.readValue(report.getTabDetails(),
                        new TypeReference<List<Tab>>() {
                        });
                dto.setTabs(tabDetails);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error parsing JSON", e);
            }
            reportDtos.add(dto);
        }
        return reportDtos;
    }

    @GetMapping("/reportRows")
    public List<DimReportRowDto> getReportRows(@RequestParam(name = "reportId") Long reportId) {
        List<DimReportRowDto> reportRows = dimReportRowRepository.findNonTotalsByReportId(reportId);
        return reportRows;
    }

    @GetMapping("/timeSeries")
    public List<TimeSeriesDto> getTimeSeries(@RequestParam(name = "year") int rptYear) {
        List<TimeSeriesDto> timeSeries = dimTimeSeriesReporsitory.findTimeSeriesByReportingYr(rptYear);
        return timeSeries;
    }
}
