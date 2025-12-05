package gov.epa.ghg.invdb.rest.dto;

import gov.epa.ghg.invdb.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntityInfo {
    private Class<? extends BaseModel> clazz;
    private String uniqueField;
}
