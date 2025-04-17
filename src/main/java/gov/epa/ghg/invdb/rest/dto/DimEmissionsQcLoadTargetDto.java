package gov.epa.ghg.invdb.rest.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimEmissionsQcLoadTargetDto {
    private Integer emissionsQcLoadTargetId;
    private Integer sourceNameId;
    private String sourceName;
    private Integer reportingYr;
    private String targetTab;
    private String rowTitleCell;
    private String anticipatedRowTitle;
    private String dateRef1990;
    private Map<String, String> emissionParameters;
    private Integer reportRowId;
    private Long reportId;
    private String rowGroup;
    private String rowSubgroup;
    private String rowTitle;
    // private Integer queryFormulaId;
    // private Map<String, String> queryFormulaParameters;
}