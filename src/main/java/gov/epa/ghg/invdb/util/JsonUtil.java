package gov.epa.ghg.invdb.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JsonUtil {
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Method to convert json file to excel file
     */
    public Workbook convertToExcel(ByteArrayInputStream input) throws IOException {
        Workbook workbook = null;
        try {
            // Create workbook
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("results");

            // Reading the json file
            ArrayNode jsonData = (ArrayNode) mapper.readTree(input.readAllBytes());
            ArrayList<String> headers = new ArrayList<String>();

            // Creating cell style for header to make it bold
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // creating the header into the sheet
            Row header = sheet.createRow(0);
            Iterator<String> it = jsonData.get(0).fieldNames();
            int headerIdx = 0;
            while (it.hasNext()) {
                String headerName = it.next();
                headers.add(headerName);
                Cell cell = header.createCell(headerIdx++);
                cell.setCellValue(headerName);
                // apply the bold style to headers
                cell.setCellStyle(headerStyle);
            }
            // Iterating over the each row data and writing into the sheet
            for (int i = 0; i < jsonData.size(); i++) {
                ObjectNode rowData = (ObjectNode) jsonData.get(i);
                if (rowData != null) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < headers.size(); j++) {
                        JsonNode valueNode = rowData.get(headers.get(j));
                        String value = (valueNode != null) ? valueNode.asText() : null;
                        row.createCell(j).setCellValue(value);
                    }
                }
            }
            /*
             * automatic adjust data in column using autoSizeColumn, autoSizeColumn should
             * be made after populating the data into the excel. Calling before populating
             * data will not have any effect.
             */
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

        } catch (IOException e) {
            log.error("Error creating excel file.", e);
            throw new IOException("Error creating excel file.", e);
        } finally {
            input.close();
        }
        return workbook;
    }

    public String parseQueryEngineResponse(String response, Long reportId) throws IOException {
        StringBuilder responseParsed = new StringBuilder();
        JsonNode jsonData = (JsonNode) mapper.readTree(response.getBytes());
        if (jsonData.get("report_id") != null && reportId.equals(jsonData.get("report_id").longValue())) {
            Iterator<JsonNode> rows = jsonData.get("query_results").elements();
            if (rows.hasNext()) {
                responseParsed.append("[");
                while (rows.hasNext()) {
                    JsonNode data = rows.next();
                    Iterator<Map.Entry<String, JsonNode>> emissions = data.get("emissions").fields();
                    while (emissions.hasNext()) {
                        Map.Entry<String, JsonNode> emission = emissions.next();
                        responseParsed.append("{");
                        responseParsed.append("\"report_row_id\": " + data.get("report_row_id") + ", ");
                        responseParsed.append("\"year_id\": " + emission.getKey() + ", ");
                        responseParsed.append("\"emission_value\": " + emission.getValue().asText());
                        responseParsed.append(" }, ");
                    }
                }
                responseParsed.deleteCharAt(responseParsed.lastIndexOf(","));// truncate the last comma from the string
                responseParsed.append("]");
            } else {
                throw new IOException("unrecognized response: " + response);
            }
        } else {
            throw new IOException(
                    "Wrong reportID in response: " + jsonData.get("report_id") + "; expected: " + reportId.toString());
        }
        return responseParsed.toString();
    }

    public String parseQcQueryEngineResponse(String response, Long reportId) throws IOException {
        ArrayNode flatRows = mapper.createArrayNode();
        JsonNode jsonData = (JsonNode) mapper.readTree(response.getBytes());
        if (jsonData.get("report_id") != null && reportId.equals(jsonData.get("report_id").longValue())) {
            JsonNode queryResults = jsonData.get("query_results");
            if (queryResults != null && queryResults.fieldNames().hasNext()) {
                Iterator<Map.Entry<String, JsonNode>> stateEntries = queryResults.fields();
                // Flatten all state rows into a single ArrayNode
                while (stateEntries.hasNext()) {
                    Map.Entry<String, JsonNode> entry = stateEntries.next();
                    String stateCode = entry.getKey();
                    JsonNode rows = entry.getValue();
                    for (JsonNode row : rows) {
                        if (row instanceof ObjectNode) {
                            ((ObjectNode) row).put("state_code", stateCode);
                        }
                        flatRows.add(row);
                    }
                }
            }
        } else {
            throw new IOException("Wrong reportID in response: " + jsonData.get("report_id").longValue()
                    + "; expected: " + reportId.toString());
        }

        StringBuilder responseParsed = new StringBuilder();
        Iterator<JsonNode> rows = flatRows.elements();
        if (rows.hasNext()) {
            responseParsed.append("[");
            while (rows.hasNext()) {
                JsonNode data = rows.next();
                if (data.has("emissions")) {
                    Iterator<Map.Entry<String, JsonNode>> emissions = data.get("emissions").fields();
                    while (emissions.hasNext()) {
                        Map.Entry<String, JsonNode> emission = emissions.next();
                        Float qcValue;
                        Float emValue;
                        if (emission.getValue() == null) {
                            emValue = 0F;
                        } else {
                            emValue = emission.getValue().floatValue();
                        }
                        if (data.has("QC")) {
                            qcValue = Math.abs(emValue - data.get("QC").get(emission.getKey()).floatValue());
                        } else {
                            log.warn("Missing QC data for row = " + data.get("report_row_id")
                                    + ", emission value will be used");
                            qcValue = Math.abs(emValue);
                        }

                        responseParsed.append("{");
                        responseParsed.append("\"qc_report_row_id\": " + data.get("report_row_id") + ", ");
                        responseParsed.append("\"year_id\": " + emission.getKey() + ", ");
                        responseParsed.append("\"qc_emission_value\": " + qcValue.toString() + ", ");
                        responseParsed.append("\"state_code\": " + data.get("state_code"));
                        responseParsed.append(" }, ");
                    }
                } else {
                    log.warn("Missing emissions data for row = " + data.get("report_row_id") + ", skipping row.");
                }
            }

            responseParsed.deleteCharAt(responseParsed.lastIndexOf(","));// truncate the last comma from the string
            responseParsed.append("]");
        } else {
            throw new IOException("unrecognized response: " + response);
        }
        return responseParsed.toString();
    }

    public byte[] generateJson(List<Map<String, Object>> headers, List<Map<String, Object>> data) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> row : data) {
            Map<String, Object> flattenedRow = new LinkedHashMap<>();
            buildFlattenedRow(flattenedRow, headers, row, "");
            result.add(flattenedRow);
        }
        // Convert to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);

        return jsonString.getBytes();
    }

    @SuppressWarnings("unchecked")
    private static void buildFlattenedRow(Map<String, Object> flattenedRow, List<Map<String, Object>> headers,
            Map<String, Object> dataRow, String parentKey) {
        for (Map<String, Object> header : headers) {
            String field = (String) header.get("field");
            String name = (String) header.get("headerName");
            List<Map<String, Object>> children = (List<Map<String, Object>>) header.get("children");

            // Build the hierarchical key
            String currentKey = parentKey.isEmpty() ? name : parentKey + "-" + name;

            if (children == null || children.isEmpty()) {
                // Leaf node, map the data value
                if (dataRow.containsKey(field)) {
                    Object value = dataRow.get(field);
                    flattenedRow.put(currentKey, value != null ? value : "");
                }
            } else {
                // Recursive call for children
                buildFlattenedRow(flattenedRow, children, dataRow, currentKey);
            }
        }
    }

    // if parentkey is empty then get all parent keys without their children
    public ArrayNode parseJsonForKeyData(String jsonFilePath, String searchKey) throws Exception {
        int keyIndex = 0;
        JsonFactory jsonFactory = new JsonFactory();
        try (JsonParser jsonParser = jsonFactory.createParser(new File(jsonFilePath))) {
            if (searchKey == null || searchKey.isEmpty()) {
                return processTarget(jsonParser);
            }
            String[] searchKeys = searchKey.split("/");
            while (jsonParser.nextToken() != null) {
                if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
                    // Get the field name to check if it is 'key'
                    String fieldName = jsonParser.getCurrentName();
                    if ("key".equals(fieldName)) {
                        jsonParser.nextToken(); // Move to the value of "key"
                        String[] keyValue = jsonParser.getText().split("/");
                        if (keyValue[keyIndex].equals(searchKeys[keyIndex])) {
                            keyIndex++;
                            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                if ("children".equals(jsonParser.getCurrentName())) {
                                    jsonParser.nextToken(); // Move to the start of the array '['
                                    break;
                                }
                            }
                            if (keyIndex == searchKeys.length) {
                                return processTarget(jsonParser);
                            }
                        } else {
                            // skip to next parent key within the hierarchy
                            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                if ("children".equals(jsonParser.getCurrentName())) {
                                    // skip children
                                    if (jsonParser.nextToken() == JsonToken.START_ARRAY) {
                                        jsonParser.skipChildren(); // This will move to the END_ARRAY
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("An error occured parsing json file ", e);
            throw e;
        }
        return null;
    }

    public JsonNode parseJsonRawData(String jsonFilePath, String searchKey) throws Exception {
        JsonFactory jsonFactory = new JsonFactory();
        try (JsonParser jsonParser = jsonFactory.createParser(new File(jsonFilePath))) {
            while (jsonParser.nextToken() != null) {
                if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
                    // Get the field name to check if it is 'key'
                    String fieldName = jsonParser.getCurrentName();
                    if ("key".equals(fieldName)) {
                        jsonParser.nextToken(); // Move to the value of "key"
                        String keyValue = jsonParser.getText();
                        if (keyValue.equals(searchKey)) {
                            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                if ("data".equals(jsonParser.getCurrentName())) {
                                    jsonParser.nextToken(); // Move to the start of the array '['
                                    JsonNode fullObject = mapper.readTree(jsonParser);
                                    return fullObject;
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("An error occured parsing json file ", e);
            throw e;
        }
        return null;
    }

    private ArrayNode processTarget(JsonParser jsonParser) throws IOException {
        // Create an empty ArrayNode to hold the filtered elements
        ArrayNode filteredElements = mapper.createArrayNode();
        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
                // Get the field name to check if it is 'key'
                String fieldName = jsonParser.getCurrentName();
                if ("key".equals(fieldName)) {
                    ObjectNode elementNode = mapper.createObjectNode();
                    jsonParser.nextToken(); // Move to the value of "key"
                    String keyValue = jsonParser.getText();
                    elementNode.put("key", keyValue);
                    // check for next field name if it is 'data'
                    if (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                        fieldName = jsonParser.getCurrentName();
                        // check if current token is data
                        if ("data".equals(fieldName)) {
                            jsonParser.nextToken();
                            JsonNode fullObject = mapper.readTree(jsonParser);
                            elementNode.set("data", fullObject);
                        }
                    }
                    if (jsonParser.nextToken() != JsonToken.END_OBJECT
                            && jsonParser.getCurrentName().equals("children")) {
                        elementNode.put("leaf", false);
                        // skip children
                        jsonParser.nextToken(); // Now we're at the start of the array/object
                        if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY
                                || jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                            jsonParser.skipChildren(); // This will move to the END_ARRAY or END_OBJECT
                        }
                        // jsonParser.nextToken(); // Move to the next field or the end of the
                        // containing object
                    } else {
                        elementNode.put("leaf", true);
                    }
                    filteredElements.add(elementNode);
                }
            }
        }
        return filteredElements;
    }

}
