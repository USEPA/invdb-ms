package gov.epa.ghg.invdb.rest.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimQcCompReportByStateDto {

    private String stateCode; // Set it to 'ALL' for reports that are not state level
    List<DimQcCompReportRowDto> reportRows;
}
