package gov.epa.ghg.invdb.rest.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class DimQcCompReportRowDto {
    private Long reportRowId;
    private Long reportId;
    private Integer rowOrder;
    private String rowGroup;
    private String rowSubgroup;
    private String rowTitle;
    private String totalsFlag;
    private String excludeFlag;
    private Integer emissionsQueryFromulaId;
    private String emissionsQueryFormula;
    private Map<String, String> emissionsQueryFormulaParameters;
    private Integer qcQueryFromulaId;
    private String qcQueryFormula;
    private Map<String, String> qcQueryFormulaParameters;

    // private Map<String, String> emissionsByYear = new HashMap<String, String>();
    private QcCompReportOutputDto[] emissions;
    private Map<Integer, Float> emissionsMap = new HashMap<Integer, Float>();

    public DimQcCompReportRowDto(Long reportRowId, Long reportId, Integer rowOrder,
            String rowGroup,
            String rowSubgroup,
            String rowTitle,
            String totalsFlag,
            String excludeFlag) {
        this.reportRowId = reportRowId;
        this.reportId = reportId;
        this.rowOrder = rowOrder;
        this.rowGroup = rowGroup;
        this.rowSubgroup = rowSubgroup;
        this.rowTitle = rowTitle;
        this.totalsFlag = totalsFlag;
        this.excludeFlag = excludeFlag;
        this.emissionsQueryFromulaId = null;
        this.emissionsQueryFormula = null;
        this.emissionsQueryFormulaParameters = null;
        this.qcQueryFromulaId = null;
        this.qcQueryFormula = null;
        this.qcQueryFormulaParameters = null;
    }

    public DimQcCompReportRowDto(Long reportRowId, Long reportId, Integer rowOrder,
            String rowGroup,
            String rowSubgroup,
            String rowTitle,
            String totalsFlag,
            String excludeFlag,
            Integer emissionsQueryFromulaId,
            String emissionsQueryFormula,
            Map<String, String> emissionsQueryFormulaParameters,
            Integer qcQueryFromulaId,
            String qcQueryFormula,
            Map<String, String> qcQueryFormulaParameters) {
        this.reportRowId = reportRowId;
        this.reportId = reportId;
        this.rowOrder = rowOrder;
        this.rowGroup = rowGroup;
        this.rowSubgroup = rowSubgroup;
        this.rowTitle = rowTitle;
        this.totalsFlag = totalsFlag;
        this.excludeFlag = excludeFlag;
        this.emissionsQueryFromulaId = emissionsQueryFromulaId;
        this.emissionsQueryFormula = emissionsQueryFormula;
        this.emissionsQueryFormulaParameters = emissionsQueryFormulaParameters;
        this.qcQueryFromulaId = qcQueryFromulaId;
        this.qcQueryFormula = qcQueryFormula;
        this.qcQueryFormulaParameters = qcQueryFormulaParameters;
    }

    public DimQcCompReportRowDto(DimQcCompReportRowDto other) {
        this.reportId = other.reportId;
        this.reportRowId = other.reportRowId;
        this.rowOrder = other.rowOrder;
        this.rowGroup = other.rowGroup;
        this.rowSubgroup = other.rowSubgroup;
        this.rowTitle = other.rowTitle;
        this.totalsFlag = other.totalsFlag;
        this.excludeFlag = other.excludeFlag;
        this.emissionsMap = new HashMap<>(other.emissionsMap);
    }
}
