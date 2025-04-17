package gov.epa.ghg.invdb.util;

import jakarta.persistence.AttributeConverter;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParamValueJsonConvertor implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null)
            return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert map to JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to map", e);
        }
    }
}
