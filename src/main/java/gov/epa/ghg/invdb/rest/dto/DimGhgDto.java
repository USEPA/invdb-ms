package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimGhgDto {
    private Integer id;
    private String ghgCode;
    private String ghgLongName;
    private Integer ghgCategoryId;
    private Float ar4Gwp;
    private Float ar5Gwp;
    private Float ar5fGwp;
    private Float ar6Gwp;
    private String ghgFormula;
    private Integer ghgrpGhgId;
    private String ghgShortname;
    private String casNo;
}