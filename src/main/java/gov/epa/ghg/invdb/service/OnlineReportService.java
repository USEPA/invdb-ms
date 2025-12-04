package gov.epa.ghg.invdb.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.epa.ghg.invdb.enumeration.AttachmentType;
import gov.epa.ghg.invdb.enumeration.ReportStatus;
import gov.epa.ghg.invdb.exception.BadRequestException;
import gov.epa.ghg.invdb.model.DimReport;
import gov.epa.ghg.invdb.model.DimTimeSeries;
import gov.epa.ghg.invdb.model.ReportOutputArComp;
import gov.epa.ghg.invdb.repository.DimReportRepository;
import gov.epa.ghg.invdb.repository.DimReportRowRepository;
import gov.epa.ghg.invdb.repository.DimTimeSeriesRepository;
import gov.epa.ghg.invdb.repository.ReportOutputArCompRepository;
import gov.epa.ghg.invdb.repository.ReportOutputRepository;
import gov.epa.ghg.invdb.rest.dto.DimReportDto;
import gov.epa.ghg.invdb.rest.dto.DimReportRowDto;
import gov.epa.ghg.invdb.rest.dto.ReportOutputArCompDto;
import gov.epa.ghg.invdb.rest.dto.ReportOutputDto;
import gov.epa.ghg.invdb.util.CsvUtil;
import gov.epa.ghg.invdb.util.ExcelUtil;
import gov.epa.ghg.invdb.util.JsonUtil;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OnlineReportService {
    @Autowired
    private ReportOutputRepository reportOutputRepository;
    @Autowired
    private DimReportRepository dimReportRepository;
    @Autowired
    private DimReportRowRepository dimReportRowRepository;
    @Autowired
    private DimTimeSeriesRepository dimTimeSeriesReporsitory;
    @Autowired
    private ReportOutputArCompRepository reportOutputArCompRepository;
    @Autowired
    private RestService restService;
    @Autowired
    private ExcelUtil excelUtil;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private CsvUtil csvUtil;

    @Value("${python.endpoint.onlineReports}")
    private String pythonEndpointUrl;
    private final int reportType = 1;

    public List<DimReportRowDto> getOnlineReportData(Long reportId) {
        List<DimReportRowDto> reportRows = dimReportRowRepository.findByReportId(reportId);
        // Get All report data
        List<ReportOutputDto> reportData = reportOutputRepository.findByReportId(reportId);
        DimReport report = dimReportRepository.findById(reportId).get();
        Map<Integer, DimTimeSeries> yearsByIdMap = getReportingYears(report.getReportingYear());

        // Map report emissions data to each row
        Map<Long, Map<Integer, ReportOutputDto>> reportDataByRow = new HashMap<Long, Map<Integer, ReportOutputDto>>();
        for (ReportOutputDto dataEntry : reportData) {
            if (reportDataByRow.get(dataEntry.getReportRowId()) == null) {
                reportDataByRow.put(dataEntry.getReportRowId(), new HashMap<Integer, ReportOutputDto>());
                try {
                    reportDataByRow.get(dataEntry.getReportRowId())
                            .put(yearsByIdMap.get(dataEntry.getReportOutputYearId()).getYear(), dataEntry);
                } catch (NullPointerException npe) {
                    log.debug("NPE caught mapping report output data by year by row");
                }
            } else {
                try {
                    reportDataByRow.get(dataEntry.getReportRowId())
                            .put(yearsByIdMap.get(dataEntry.getReportOutputYearId()).getYear(), dataEntry);
                } catch (NullPointerException npe) {
                    log.debug("NPE caught mapping report output data by year by row");
                }
            }
        }

        for (DimReportRowDto reportRow : reportRows) {
            for (DimTimeSeries year : yearsByIdMap.values()) {
                Float emissionValue = 0F;
                try {
                    emissionValue = reportDataByRow.get(reportRow.getReportRowId()).get(year.getYear())
                            .getReportOutputValue();
                } catch (NullPointerException npe) {
                    log.debug("NPE caught mapping report output data to rows");
                }
                reportRow.getEmissionsMap().put(year.getYear(), emissionValue);
            }
        }

        // make sure rows are sorted by row_order before returning
        Collections.sort(reportRows, (row1, row2) -> row1.getRowOrder() - row2.getRowOrder());
        return reportRows;
    }

    public String generateEmissionsReportCsv(Long reportId) {
        DimReport report = dimReportRepository.findById(reportId).get();
        String rowHeader = report.getReportRowsHeader();
        List<DimReportRowDto> reportRows = getOnlineReportData(reportId);
        Map<Integer, DimTimeSeries> yearsByIdMap = getReportingYears(report.getReportingYear());
        List<DimTimeSeries> reportingYears = dimTimeSeriesReporsitory.findAll();
        Collections.sort(reportingYears, (p1, p2) -> {
            return (Integer.valueOf(p1.getYearId() - p2.getYearId())).intValue();
        });
        StringBuilder refinedData = new StringBuilder();
        refinedData.append("\"" + rowHeader + "\"");
        for (DimTimeSeries year : reportingYears) {
            if (yearsByIdMap.containsKey(year.getYearId())) {
                refinedData.append("," + year.getYear());
            }
        }
        refinedData.append(System.lineSeparator());

        for (DimReportRowDto reportRow : reportRows) {
            refinedData.append("\"" + reportRow.getRowTitle() + "\"");
            if (reportRow.getEmissionsMap() != null) {
                for (DimTimeSeries year : reportingYears) {
                    if (reportRow.getEmissionsMap().get(year.getYear()) != null) {
                        refinedData.append("," + reportRow.getEmissionsMap().get(year.getYear()));
                    } else if (yearsByIdMap.containsKey(year.getYearId())) {
                        refinedData.append(",0.0");
                    }
                }
            }
            refinedData.append(System.lineSeparator());
        }
        return refinedData.toString();
    }

    public byte[] generateEmissionsReportExcel(Long reportId) throws IOException {
        String reportJson = generateEmissionsReportJson(reportId);
        Workbook workbook = jsonUtil.convertToExcel(new ByteArrayInputStream(reportJson.getBytes()));
        byte[] output = excelUtil.getBytes(workbook);
        workbook.close();
        return output;
    }

    public String generateEmissionsReportJson(Long reportId) {
        DimReport report = dimReportRepository.findById(reportId).get();
        String rowHeader = report.getReportRowsHeader();
        List<DimReportRowDto> reportRows = getOnlineReportData(reportId);
        List<DimTimeSeries> reportingYears = dimTimeSeriesReporsitory.findAll();
        Map<Integer, DimTimeSeries> yearsByIdMap = getReportingYears(report.getReportingYear());

        Collections.sort(reportingYears, (p1, p2) -> {
            return (Integer.valueOf(p1.getYearId() - p2.getYearId())).intValue();
        });
        StringBuilder refinedData = new StringBuilder();
        refinedData.append("[" + System.lineSeparator());
        for (DimReportRowDto reportRow : reportRows) {
            refinedData.append("    {" + System.lineSeparator());
            refinedData.append("        \"" + rowHeader + "\" : \"" + reportRow.getRowTitle() + "\"");
            if (reportRow.getEmissionsMap() != null) {
                for (DimTimeSeries year : reportingYears) {
                    if (reportRow.getEmissionsMap().get(year.getYear()) != null) {
                        refinedData.append(", " + System.lineSeparator());
                        refinedData.append("        \"" + year.getYear() + "\": \""
                                + reportRow.getEmissionsMap().get(year.getYear()) + "\"");
                    } else if (yearsByIdMap.containsKey(year.getYearId())) {
                        refinedData.append(", " + System.lineSeparator());
                        refinedData.append("        \"" + year.getYear() + "\" : \"" + "0.0" + "\"");
                    }
                }
            }
            refinedData.append(System.lineSeparator() + "    }," + System.lineSeparator());
        }
        refinedData.deleteCharAt(refinedData.lastIndexOf(","));// truncate the last comma from the string
        refinedData.append("]");

        return refinedData.toString();
    }

    public Map<Integer, DimTimeSeries> getReportingYears(int selectedYear) {
        List<DimTimeSeries> reportingYears = dimTimeSeriesReporsitory.findAll();

        Map<Integer, DimTimeSeries> yearsByIdMap = new LinkedMap<Integer, DimTimeSeries>();
        for (DimTimeSeries year : reportingYears) {
            if (year.getYear() <= selectedYear - 2) {
                yearsByIdMap.put(year.getYearId(), year);
            }
        }
        return yearsByIdMap;
    }

    public DimReport refreshOnlineReport(Long reportId, int userId) {
        String response;
        Optional<DimReport> option = dimReportRepository.findById(reportId);
        DimReport report = option.isPresent() ? option.get() : null;
        if (report != null) {
            try {
                updateReportRefreshStatus(report, ReportStatus.PROCESSING_QUERIES, userId);
                String uriWithParams = String.format(pythonEndpointUrl, reportId, reportType, userId);
                log.debug("Generate Online Reports URL: " + uriWithParams);
                response = restService.invokeRestClient(uriWithParams).getBody(); // Call python procedure to generate
                                                                                  // the report

                String responseParsed = jsonUtil.parseQueryEngineResponse(response, reportId); // includes basic
                                                                                               // response validation
                                                                                               // check, may throw
                                                                                               // IOException

                updateReportRefreshStatus(report, ReportStatus.PROCESSING_DATA, userId);
                // success = reportOutputRepository.populateQueryResponse(responseParsed,
                // userId);
                reportOutputRepository.populateReportOutputTables(responseParsed, reportId, userId);

                // get refreshed report
                report = dimReportRepository.findById(reportId).get();

                // if (StringUtils.equalsIgnoreCase(success, "success")) {
                // updateReportRefreshStatus(report, ReportStatus.PROCESSING_TOTALS, userId);
                // success = reportOutputRepository.computeSubtotals(reportId.intValue(),
                // userId);
                // if (StringUtils.equalsIgnoreCase(success, "success")) {
                // updateReportRefreshStatus(report, ReportStatus.READY, userId);
                // } else {
                // log.error("Error computing totals for report " + report.getReportName());
                // updateReportRefreshStatus(report, ReportStatus.ERROR, userId);
                // }
                // } else {
                // log.error("Error updating data for report: " + report.getReportName());
                // updateReportRefreshStatus(report, ReportStatus.ERROR, userId);
                // }
            } catch (Exception e) {
                log.warn("Exception caught trying to refresh online report: ", e);
                updateReportRefreshStatus(report, ReportStatus.ERROR, userId);
            }
        }
        return report;
    }

    @Transactional
    public void updateReportRefreshStatus(DimReport report, ReportStatus status, int userId) {
        Date currentTimestamp = new Date();
        report.setRefreshStatus(status.getValue());
        report.setLastUpdatedDate(currentTimestamp);
        report.setLastUpdatedBy(userId);
        if (status.equals(ReportStatus.READY)) {
            report.setReportRefreshDate(currentTimestamp);
        }
        dimReportRepository.save(report);
    }

    public String getReportStatus(Long reportId) {
        String reportStatus;
        Optional<DimReport> option = dimReportRepository.findById(reportId);
        DimReport report = option.isPresent() ? option.get() : null;
        if (report != null) {
            reportStatus = report.getRefreshStatus();
        } else {
            reportStatus = ReportStatus.ERROR.getValue();
        }
        return reportStatus;
    }

    public DimReportDto getReportById(Long reportId) {
        Optional<DimReport> option = dimReportRepository.findById(reportId);
        DimReport report = option.isPresent() ? option.get() : null;
        return new DimReportDto(report);
    }

    public List<ReportOutputArCompDto> getArComparisonData(Long reportId, String arOption, int userId) {
        String response;
        List<ReportOutputArCompDto> arCompData = null;
        try {
            // check if ar comparison values are latest
            boolean isLatest = reportOutputArCompRepository.findArComparisonDataLatest(reportId, arOption)
                    .orElse(false);
            if (isLatest) {
                arCompData = reportOutputArCompRepository.getReportOutputArCompData(reportId, arOption);
            } else {
                String uriWithParams = String.format(pythonEndpointUrl, reportId, reportType, userId) + "&gwp="
                        + arOption;
                log.debug("Generate Online Reports URL: " + uriWithParams);
                // Call python procedure to generate the report
                response = restService.invokeRestClient(uriWithParams).getBody();
                // includes basic response validation check, may throw IOException
                String responseParsed = jsonUtil.parseQueryEngineResponse(response, reportId);
                arCompData = reportOutputArCompRepository.getReportOutputArCompData(responseParsed, arOption,
                        userId);
            }
        } catch (Exception e) {
            log.warn("Exception caught trying to generate ar comparison report ", e);

        }
        return arCompData;
    }

    public byte[] generateEmissionsReportForDownload(List<Map<String, Object>> headers, List<Map<String, Object>> data,
            String format)
            throws Exception {
        byte[] result;
        if (format.equalsIgnoreCase(AttachmentType.JSON.name())) {
            result = jsonUtil.generateJson(headers, data);
        } else if (format.equalsIgnoreCase(AttachmentType.EXCEL.name())) {
            result = excelUtil.generateExcel(headers, data);
        } else if (format.equalsIgnoreCase(AttachmentType.CSV.name())) {
            result = csvUtil.generateCsv(headers, data);
        } else {
            throw new BadRequestException("Unrecognizable download format");
        }
        return result;
    }

}
