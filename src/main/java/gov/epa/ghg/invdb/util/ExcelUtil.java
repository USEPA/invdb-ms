package gov.epa.ghg.invdb.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class ExcelUtil {

    public byte[] getBytes(Workbook workbook) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } catch (IOException e) {
            log.error("Error reading workbook: ", e);
            throw new IOException("Error reading workbook.", e);
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }

    public Optional<String> readCsvCell(MultipartFile file, int rowIndex, int columnIndex) {
        Optional<String> ret = Optional.empty();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int currentRow = 0;

            int rowIdx = rowIndex - 1;
            int columnIdx = columnIndex - 1;

            while ((line = br.readLine()) != null) {
                if (currentRow == rowIdx) {
                    String[] columns = line.split(",");
                    if (columnIdx < columns.length) {
                        ret = Optional.of(columns[columnIdx].trim());
                        break;
                    } else {
                        ret = Optional.empty(); // Column out of bounds
                        break;
                    }
                }
                currentRow++;
            }

            if (ret == null || ret.isEmpty()) {
                return null;
            }

            String csvValue = getCsvCellValue(ret.get());
            return Optional.ofNullable(csvValue);

        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<String> readCell(MultipartFile file, String sheetName, int rowIndex, int columnIndex)
            throws IOException {
        try (InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IOException("Expected worksheet \"" + sheetName + "\" was not found in the uploaded workbook."
                        + "  Please confirm you are uploading the correct Reporting Form.");
            }
            Row row = sheet.getRow(rowIndex - 1);
            Cell cell = row.getCell(columnIndex - 1);

            String cellValue = getCellValue(cell, evaluator);
            return Optional.ofNullable(cellValue != null ? cellValue : null);
        }
    }

    public Map<String, String> convertToJson(MultipartFile file, String sheetName) throws IOException {
        Map<String, String> resultMap = new HashMap<>();
        try (InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IOException("Expected worksheet \"" + sheetName + "\" was not found in the uploaded workbook."
                        + "  Please confirm you are uploading the correct Reporting Form.");
            }
            // Get headers
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            // Iterate over rows (excluding header row)
            List<Map<String, Object>> data = new ArrayList<>();
            Integer noOfRecords = 0;
            boolean isRowBlank;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    isRowBlank = true;
                    Map<String, Object> rowData = new HashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        // INVDB-530
                        // Added a check to see if the cell value isEmpty to the if statement
                        // This fixed the issue where Subsector by State and Economic Sector by State
                        // Were importing empty rows
                        String cellVal = getCellValue(cell, evaluator);
                        if (cell != null && cell.getCellType() != CellType.BLANK && !cellVal.isEmpty()) {
                            rowData.put(headers.get(j), cellVal);
                            isRowBlank = false;
                        }
                    }
                    if (!isRowBlank) {
                        noOfRecords++;
                        data.add(rowData);
                    }
                }
            }
            // Convert to JSON using Jackson ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper()
                    .configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true)
                    .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true); // Ignore unknown JSON features
            resultMap.put("jsonAsString", objectMapper.writeValueAsString(data));
            resultMap.put("noOfRecords", String.valueOf(noOfRecords));
            return resultMap;
        }

    }

    private String getCellValue(Cell cell, FormulaEvaluator formulaEvaluator) {
        if (cell == null) {
            return null;
        }
        String value;
        switch (cell.getCellType()) {
            case STRING:
                value = cell.getStringCellValue().replace("\n", "\\n").replace("\r", "\\r");
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue().toString(); // Format date as needed
                } else {
                    value = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                CellValue evaluatedValue = formulaEvaluator.evaluate(cell);
                // Get the string representation of the evaluated value
                switch (evaluatedValue.getCellType()) {
                    case NUMERIC:
                        value = String.valueOf(evaluatedValue.getNumberValue()); // Get numeric value
                        break;
                    case STRING:
                        value = evaluatedValue.getStringValue().replace("\n", "\\n").replace("\r", "\\r"); // Get
                        // string
                        // value
                        break;
                    case BOOLEAN:
                        value = String.valueOf(evaluatedValue.getBooleanValue()); // Get boolean value
                        break;
                    case FORMULA: // Shouldn't happen after evaluation
                        value = "Formula Error"; // Handle potential errors
                        break;
                    case ERROR:
                        value = "Error"; // Handle formula errors
                        break;
                    default:
                        value = evaluatedValue.toString(); // Default case (date, etc.)
                        break;
                }
                break;
            default:
                value = null;
        }
        return value;
    }

    private String getCsvCellValue(String cellValue) {
        // numerics
        try {
            double numericValue = Double.parseDouble(cellValue);
            return String.valueOf(numericValue);
        } catch (NumberFormatException e) {
            // Not a numeric value, fall back to treating it as a string
        }

        // booleans
        if (cellValue.equalsIgnoreCase("true") || cellValue.equalsIgnoreCase("false")) {
            return cellValue.toLowerCase();
        }

        // special newlines, etc
        return cellValue.replace("\n", "\\n").replace("\r", "\\r");
    }

    public byte[] generateExcel(List<Map<String, Object>> headers, List<Map<String, Object>> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data with Groups");

            // Extract fields and map data
            List<String> fields = extractFields(headers);
            List<List<Object>> orderedData = mapDataToFields(data, fields);
            // Create headers
            createHeaders(sheet, headers, 0, 0);

            // Add data rows
            int dataRowStart = getMaxDepth(headers);
            for (int i = 0; i < orderedData.size(); i++) {
                Row dataRow = sheet.createRow(dataRowStart + i);
                List<Object> rowData = orderedData.get(i);
                for (int j = 0; j < rowData.size(); j++) {
                    Cell cell = dataRow.createCell(j);
                    Object value = rowData.get(j);
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else {
                        cell.setCellValue(value == null ? "" : value.toString());
                    }
                }
            }
            byte[] output = getBytes(workbook);
            workbook.close();
            return output;
        }
    }

    @SuppressWarnings("unchecked")
    private int createHeaders(Sheet sheet, List<Map<String, Object>> columns, int depth, int colIndex) {
        Row headerRow = sheet.getRow(depth);
        if (headerRow == null) {
            headerRow = sheet.createRow(depth);
        }

        for (Map<String, Object> column : columns) {
            String headerName = column.get("headerName").toString();
            List<Map<String, Object>> children = (List<Map<String, Object>>) column.get("children");

            if (children != null && children.size() > 0) {
                // Merge parent header cell
                int startCol = colIndex;
                colIndex = createHeaders(sheet, children, depth + 1, colIndex);
                Cell parentCell = headerRow.createCell(startCol);
                parentCell.setCellValue(headerName);
                if (children.size() > 1) { // Merged region must contain 2 or more cells
                    sheet.addMergedRegion(new CellRangeAddress(depth, depth, startCol, colIndex - 1));
                }
            } else {
                // Create leaf cell
                Cell cell = headerRow.createCell(colIndex);
                cell.setCellValue(headerName);
                colIndex++;
            }
        }

        return colIndex;
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

    // To ensure the data matches the header sequence, reorder it dynamically:
    private List<List<Object>> mapDataToFields(List<Map<String, Object>> data, List<String> fields) {
        List<List<Object>> rows = new ArrayList<>();
        for (Map<String, Object> row : data) {
            List<Object> orderedRow = new ArrayList<>();
            for (String field : fields) {
                orderedRow.add(row.getOrDefault(field, "")); // Add data or empty string if missing
            }
            rows.add(orderedRow);
        }
        return rows;
    }

    @SuppressWarnings({ "unchecked" })
    private int getMaxDepth(List<Map<String, Object>> columns) {
        int maxDepth = 1; // A single level of header exists even without children
        for (Map<String, Object> column : columns) {
            List<Map<String, Object>> children = (List<Map<String, Object>>) column.get("children");
            if (children != null && children.size() > 0) {
                // Recursively calculate depth of child headers
                maxDepth = Math.max(maxDepth, 1 + getMaxDepth(children));
            }
        }
        return maxDepth;
    }
}
