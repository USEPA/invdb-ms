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

import gov.epa.ghg.invdb.enumeration.ReportStatus;
import gov.epa.ghg.invdb.model.DimQcReport;
import gov.epa.ghg.invdb.model.DimTimeSeries;
import gov.epa.ghg.invdb.repository.DimQcCompReportRowRepository;
import gov.epa.ghg.invdb.repository.DimQcReportRepository;
import gov.epa.ghg.invdb.repository.DimTimeSeriesRepository;
import gov.epa.ghg.invdb.repository.QcCompReportOutputRepository;
import gov.epa.ghg.invdb.rest.dto.DimQcCompReportRowDto;
import gov.epa.ghg.invdb.rest.dto.DimQcReportDto;
import gov.epa.ghg.invdb.rest.dto.QcCompReportOutputDto;
import gov.epa.ghg.invdb.util.ExcelUtil;
import gov.epa.ghg.invdb.util.JsonUtil;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class QcReportService {
    @Autowired
    private QcCompReportOutputRepository qcRepository;
    @Autowired
    private DimQcReportRepository dimQcReportRepository;
    @Autowired
    private DimQcCompReportRowRepository dimQcCompReportRowRepository;
    @Autowired
    private DimTimeSeriesRepository dimTimeSeriesReporsitory;
    @Autowired
    private RestService restService;
    @Autowired
    private ExcelUtil excelUtil;
    @Autowired
    private JsonUtil jsonUtil;

    @Value("${python.endpoint.onlineReports}")
    private String pythonEndpointUrl;
    private final int reportType = 2;

    public List<DimQcCompReportRowDto> getQcReportData(Long reportId) {
        List<DimQcCompReportRowDto> reportRows = dimQcCompReportRowRepository.findByReportId(reportId);
        // Get All report data
        List<QcCompReportOutputDto> reportData = qcRepository.findByReportId(reportId);
        DimQcReport report = dimQcReportRepository.findById(reportId).get();
        Map<Integer, DimTimeSeries> yearsByIdMap = getReportingYears(report.getReportingYear());

        // Map report emissions data to each row
        Map<Long, Map<Integer, QcCompReportOutputDto>> reportDataByRow = new HashMap<Long, Map<Integer, QcCompReportOutputDto>>();
        for (QcCompReportOutputDto dataEntry : reportData) {
            if (reportDataByRow.get(dataEntry.getReportRowId()) == null) {
                reportDataByRow.put(dataEntry.getReportRowId(), new HashMap<Integer, QcCompReportOutputDto>());
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

        for (DimQcCompReportRowDto reportRow : reportRows) {
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

    public String generateQcReportCsv(Long reportId) {
        DimQcReport report = dimQcReportRepository.findById(reportId).get();
        String rowHeader = report.getReportRowsHeader();
        List<DimQcCompReportRowDto> reportRows = getQcReportData(reportId);
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

        for (DimQcCompReportRowDto reportRow : reportRows) {
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

    public byte[] generateQcReportExcel(Long reportId) throws IOException {
        String reportJson = generateQcReportJson(reportId);
        Workbook workbook = jsonUtil.convertToExcel(new ByteArrayInputStream(reportJson.getBytes()));
        byte[] output = excelUtil.getBytes(workbook);
        workbook.close();
        return output;
    }

    public String generateQcReportJson(Long reportId) {
        DimQcReport report = dimQcReportRepository.findById(reportId).get();
        String rowHeader = report.getReportRowsHeader();
        List<DimQcCompReportRowDto> reportRows = getQcReportData(reportId);
        List<DimTimeSeries> reportingYears = dimTimeSeriesReporsitory.findAll();
        Map<Integer, DimTimeSeries> yearsByIdMap = getReportingYears(report.getReportingYear());

        Collections.sort(reportingYears, (p1, p2) -> {
            return (Integer.valueOf(p1.getYearId() - p2.getYearId())).intValue();
        });
        StringBuilder refinedData = new StringBuilder();
        refinedData.append("[" + System.lineSeparator());
        for (DimQcCompReportRowDto reportRow : reportRows) {
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

    public DimQcReport refreshQcReport(Long reportId, int userId) {
        String success = "fail";
        String response;
        Optional<DimQcReport> option = dimQcReportRepository.findById(reportId);
        DimQcReport report = option.isPresent() ? option.get() : null;
        if (report != null) {
            try {
                updateReportRefreshStatus(report, ReportStatus.PROCESSING_QUERIES, userId);
                // Call python procedure to generate the report
                // TO-DO: externalize strings
                String uriWithParams = String.format(pythonEndpointUrl, reportId, reportType, userId);
                log.debug("Generate Online Reports URL: " + uriWithParams);
                response = restService.invokeRestClient(uriWithParams).getBody();

                updateReportRefreshStatus(report, ReportStatus.PROCESSING_DATA, userId);
                String responseParsed = jsonUtil.parseQcQueryEngineResponse(response, reportId); // includes basic
                                                                                                 // response validation
                                                                                                 // check, may throw
                                                                                                 // IOException

                success = qcRepository.populateQcQueryResponse(responseParsed, userId);

                if (StringUtils.equalsIgnoreCase(success, "success")) {
                    updateReportRefreshStatus(report, ReportStatus.READY, userId);
                } else {
                    log.error("Error updating data for report: " + report.getReportName());
                    updateReportRefreshStatus(report, ReportStatus.ERROR, userId);
                }
            } catch (Exception e) {
                log.warn("Exception caught trying to refresh online report: ", e);
                updateReportRefreshStatus(report, ReportStatus.ERROR, userId);
            }
        }
        return report;
    }

    @Transactional
    public void updateReportRefreshStatus(DimQcReport report, ReportStatus status, int userId) {
        Date currentTimestamp = new Date();
        report.setRefreshStatus(status.getValue());
        report.setLastUpdatedDate(currentTimestamp);
        report.setLastUpdatedBy(userId);
        if (status.equals(ReportStatus.READY)) {
            report.setReportRefreshDate(currentTimestamp);
        }
        dimQcReportRepository.save(report);
    }

    public DimQcReportDto getReportById(Long reportId) {
        Optional<DimQcReport> option = dimQcReportRepository.findById(reportId);
        DimQcReport report = option.isPresent() ? option.get() : null;
        return new DimQcReportDto(report);
    }

}
