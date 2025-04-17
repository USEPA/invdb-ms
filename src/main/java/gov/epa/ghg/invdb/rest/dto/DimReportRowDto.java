package gov.epa.ghg.invdb.rest.dto;

import java.util.Map;
import java.util.HashMap;

import lombok.Data;

@Data
public class DimReportRowDto {
    private Long reportRowId;
    private Long reportId;
    private Integer rowOrder;
    private String rowGroup;
    private String rowSubgroup;
    private String rowTitle;
    private String totalsFlag;
    private String excludeFlag;
    private Integer queryFromulaId;
    private String queryFormula;
    private Map<String, String> queryFormulaParameters;

    private Map<Integer, Float> emissionsMap = new HashMap<Integer, Float>();
    private Map<Integer, Float> emissionChangeMap = new HashMap<Integer, Float>();

    public DimReportRowDto(Long reportRowId, Long reportId, Integer rowOrder,
            String rowGroup,
            String rowSubgroup,
            String rowTitle,
            String totalsFlag,
            String excludeFlag,
            Integer queryFromulaId,
            String queryFormula,
            Map<String, String> queryFormulaParameters) {
        this.reportRowId = reportRowId;
        this.reportId = reportId;
        this.rowOrder = rowOrder;
        this.rowGroup = rowGroup;
        this.rowSubgroup = rowSubgroup;
        this.rowTitle = rowTitle;
        this.totalsFlag = totalsFlag;
        this.excludeFlag = excludeFlag;
        this.queryFromulaId = queryFromulaId;
        this.queryFormula = queryFormula;
        this.queryFormulaParameters = queryFormulaParameters;
    }
}
