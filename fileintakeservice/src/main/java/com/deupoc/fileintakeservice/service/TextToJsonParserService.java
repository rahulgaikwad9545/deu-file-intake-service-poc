package com.deupoc.fileintakeservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Service for parsing text files into JSON format.
 * Supports various text file formats including CSV, TSV, and custom delimited formats.
 */
@Service
@Slf4j
public class TextToJsonParserService {

    private final ObjectMapper objectMapper;

    public TextToJsonParserService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Parse text content to JSON based on the specified format
     * @param textContent the text content to parse
     * @param format the format of the text file (CSV, TSV, etc.)
     * @param hasHeaders whether the first line contains headers
     * @return JSON string representation
     * @throws IOException if parsing fails
     */
    public String parseTextToJson(String textContent, String format, boolean hasHeaders) throws IOException {
        log.info("Parsing text content to JSON with format: {}", format);

        try {
            switch (format.toUpperCase()) {
                case "CSV":
                    return parseCsvToJson(textContent, hasHeaders);
                case "TSV":
                    return parseTsvToJson(textContent, hasHeaders);
                case "PIPE":
                    return parseDelimitedToJson(textContent, "|", hasHeaders);
                case "SEMICOLON":
                    return parseDelimitedToJson(textContent, ";", hasHeaders);
                case "CUSTOM":
                    return parseCustomFormatToJson(textContent);
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (Exception e) {
            log.error("Error parsing text to JSON", e);
            throw new IOException("Failed to parse text content to JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Parse CSV content to JSON
     * @param csvContent the CSV content
     * @param hasHeaders whether first line contains headers
     * @return JSON string
     * @throws IOException if parsing fails
     */
    private String parseCsvToJson(String csvContent, boolean hasHeaders) throws IOException {
        return parseDelimitedToJson(csvContent, ",", hasHeaders);
    }

    /**
     * Parse TSV content to JSON
     * @param tsvContent the TSV content
     * @param hasHeaders whether first line contains headers
     * @return JSON string
     * @throws IOException if parsing fails
     */
    private String parseTsvToJson(String tsvContent, boolean hasHeaders) throws IOException {
        return parseDelimitedToJson(tsvContent, "\t", hasHeaders);
    }

    /**
     * Parse delimited text content to JSON
     * @param content the delimited content
     * @param delimiter the delimiter character
     * @param hasHeaders whether first line contains headers
     * @return JSON string
     * @throws IOException if parsing fails
     */
    private String parseDelimitedToJson(String content, String delimiter, boolean hasHeaders) throws IOException {
        List<Map<String, String>> records = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new StringReader(content));
        String line;
        String[] headers = null;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            String[] values = line.split(delimiter, -1); // -1 to include empty values

            if (hasHeaders && lineNumber == 1) {
                headers = values;
                continue;
            }

            Map<String, String> record = new LinkedHashMap<>();
            if (headers != null && headers.length > 0) {
                for (int i = 0; i < Math.min(values.length, headers.length); i++) {
                    record.put(headers[i].trim(), values[i].trim());
                }
                // Add empty values for missing columns
                for (int i = values.length; i < headers.length; i++) {
                    record.put(headers[i].trim(), "");
                }
            } else {
                // No headers, use column indices
                for (int i = 0; i < values.length; i++) {
                    record.put("column_" + (i + 1), values[i].trim());
                }
            }

            record.put("line_number", String.valueOf(lineNumber));
            records.add(record);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total_records", records.size());
        result.put("data", records);

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
    }

    /**
     * Parse custom format text to JSON
     * This is a basic implementation that treats each line as a separate record
     * @param content the custom format content
     * @return JSON string
     * @throws IOException if parsing fails
     */
    private String parseCustomFormatToJson(String content) throws IOException {
        List<Map<String, String>> records = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new StringReader(content));
        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            Map<String, String> record = new LinkedHashMap<>();
            record.put("line_number", String.valueOf(lineNumber));
            record.put("content", line.trim());
            records.add(record);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total_records", records.size());
        result.put("data", records);

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
    }

    /**
     * Validate if the parsed JSON is valid
     * @param jsonString the JSON string to validate
     * @return true if valid JSON, false otherwise
     */
    public boolean isValidJson(String jsonString) {
        try {
            objectMapper.readTree(jsonString);
            return true;
        } catch (Exception e) {
            log.warn("Invalid JSON: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract specific field from JSON
     * @param jsonString the JSON string
     * @param fieldPath the path to the field (e.g., "data[0].name")
     * @return the field value or null if not found
     */
    public String extractFieldFromJson(String jsonString, String fieldPath) {
        try {
            JsonNode root = objectMapper.readTree(jsonString);
            String[] pathParts = fieldPath.split("\\.");

            JsonNode current = root;
            for (String part : pathParts) {
                if (part.contains("[")) {
                    String arrayName = part.substring(0, part.indexOf("["));
                    int index = Integer.parseInt(part.substring(part.indexOf("[") + 1, part.indexOf("]")));
                    current = current.get(arrayName).get(index);
                } else {
                    current = current.get(part);
                }

                if (current == null) {
                    return null;
                }
            }

            return current.asText();
        } catch (Exception e) {
            log.error("Error extracting field from JSON", e);
            return null;
        }
    }
}