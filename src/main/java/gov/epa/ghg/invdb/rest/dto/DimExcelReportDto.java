package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimExcelReportDto {
    private Integer id;
    private String reportName;
    private String filename;
    private Long fileSize;
    private String fileType;
}