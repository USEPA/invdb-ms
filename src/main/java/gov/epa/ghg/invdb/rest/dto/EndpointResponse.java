package gov.epa.ghg.invdb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndpointResponse<T> {

	protected T result;
	protected String responseMessageText;
}
