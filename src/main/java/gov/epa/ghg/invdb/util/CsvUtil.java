package gov.epa.ghg.invdb.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import java.nio.charset.StandardCharsets;

@Component
@Log4j2
public class CsvUtil {
    public byte[] generateCsv(List<Map<String, Object>> headers, List<Map<String, Object>> data) throws IOException {
        StringBuilder csvBuilder = new StringBuilder();

        // Extract fields and map data
        List<String> fields = extractFields(headers);

        // Create headers
        List<String> flattenedHeaders = generateHeaders(headers, "");
        // Write headers
        csvBuilder.append(String.join(",", flattenedHeaders)).append("\n");

        // Write data rows
        // writeDataRows(csvBuilder, orderedData, fields);
        for (Map<String, Object> row : data) {
            List<String> rowData = fields.stream()
                    .map(field -> {
                        Object value = row.get(field);
                        return value != null ? value.toString() : "";
                    })
                    .toList();
            csvBuilder.append(String.join(",", rowData)).append("\n");
        }

        // Convert CSV content to byte array
        byte[] csvBytes = csvBuilder.toString().getBytes(StandardCharsets.UTF_8);

        return csvBytes;
    }

    /*
     * Since CSV is a plain-text format, you'll need to represent the hierarchical
     * column groups in a flat structure.
     * For example:
     * Excel grouped structure:
     * Group 1 | Group 2
     * Year | Sales | Region | Revenue
     * CSV flattened structure:
     * Group 1 - Year, Group 1 - Sales, Group 2 - Region, Group 2 - Revenue
     */
    @SuppressWarnings("unchecked")
    public List<String> generateHeaders(List<Map<String, Object>> columns, String parent) {
        List<String> headers = new ArrayList<>();

        for (Map<String, Object> column : columns) {
            String currentHeader = (parent == null || parent.isEmpty())
                    ? column.get("headerName").toString()
                    : parent + " - " + column.get("headerName").toString();
            List<Map<String, Object>> children = (List<Map<String, Object>>) column.get("children");

            if (children != null && children.size() > 0) {
                // Recursively process children
                headers.addAll(generateHeaders(children, currentHeader));
            } else {
                // Add the column if no children exist
                headers.add(currentHeader);
            }
        }
        return headers;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractFields(List<Map<String, Object>> columns) {
        List<String> fields = new ArrayList<>();
        for (Map<String, Object> column : columns) {
            List<Map<String, Object>> children = (List<Map<String, Object>>) column.get("children");
            if (children != null && children.size() > 0) {
                fields.addAll(extractFields(children)); // Recursively process children
            } else if (column.get("field") != null) {
                fields.add(column.get("field").toString());
            }
        }
        return fields;
    }
}
