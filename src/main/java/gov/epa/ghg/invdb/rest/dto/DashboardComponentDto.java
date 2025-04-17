package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardComponentDto {
    private Integer componentId;
    private String componentName;
    private String componentLabel;
    private String componentStatus;
    private Integer parentComponentId;
}