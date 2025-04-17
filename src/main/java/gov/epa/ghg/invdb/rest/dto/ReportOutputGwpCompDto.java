package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportOutputGwpCompDto {
    private Long reportRowId;
    private Float firstGwp;
    private Float firstPercent;
    private Float firstAbsolute;
    private Float secondGwp;
    private Float secondPercent;
    private Float secondAbsolute;
}
