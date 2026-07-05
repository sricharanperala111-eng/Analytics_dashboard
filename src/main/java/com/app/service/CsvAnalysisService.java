package com.app.service;

import com.app.model.AnalysisResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CsvAnalysisService {

    public AnalysisResult analyze(MultipartFile file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        String[] headers;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) throw new IllegalArgumentException("Empty file");
            headers = splitCsvLine(headerLine);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                rows.add(splitCsvLine(line));
            }
        }

        int colCount = headers.length;

        // Determine which columns are numeric by sampling all rows
        boolean[] isNumeric = new boolean[colCount];
        Arrays.fill(isNumeric, true);

        for (String[] row : rows) {
            for (int c = 0; c < colCount && c < row.length; c++) {
                if (!isParsableNumber(row[c])) {
                    isNumeric[c] = false;
                }
            }
        }

        List<String> numericColumns = new ArrayList<>();
        for (int c = 0; c < colCount; c++) {
            if (isNumeric[c]) numericColumns.add(headers[c]);
        }

        // Pick the first non-numeric column as the "category" column (for grouping),
        // and the first numeric column as the primary metric for that breakdown.
        String categoryColumn = null;
        for (int c = 0; c < colCount; c++) {
            if (!isNumeric[c]) { categoryColumn = headers[c]; break; }
        }

        Map<String, AnalysisResult.ColumnStats> stats = new LinkedHashMap<>();
        Map<String, Double> categoryBreakdown = new LinkedHashMap<>();

        String primaryMetric = numericColumns.isEmpty() ? null : numericColumns.get(0);

        for (int c = 0; c < colCount; c++) {
            if (!isNumeric[c]) continue;
            String col = headers[c];
            double sum = 0, min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
            int count = 0;
            for (String[] row : rows) {
                if (c >= row.length) continue;
                double val = parseNumber(row[c]);
                sum += val;
                min = Math.min(min, val);
                max = Math.max(max, val);
                count++;
            }
            double avg = count == 0 ? 0 : sum / count;
            stats.put(col, new AnalysisResult.ColumnStats(round(sum), round(avg), count == 0 ? 0 : round(min), count == 0 ? 0 : round(max)));
        }

        if (categoryColumn != null && primaryMetric != null) {
            int catIdx = indexOf(headers, categoryColumn);
            int metricIdx = indexOf(headers, primaryMetric);
            for (String[] row : rows) {
                if (catIdx >= row.length || metricIdx >= row.length) continue;
                String key = row[catIdx];
                double val = parseNumber(row[metricIdx]);
                categoryBreakdown.merge(key, val, Double::sum);
            }
            // round values
            categoryBreakdown.replaceAll((k, v) -> round(v));
        }

        List<Map<String, String>> preview = new ArrayList<>();
        for (int i = 0; i < Math.min(10, rows.size()); i++) {
            String[] row = rows.get(i);
            Map<String, String> rowMap = new LinkedHashMap<>();
            for (int c = 0; c < colCount; c++) {
                rowMap.put(headers[c], c < row.length ? row[c] : "");
            }
            preview.add(rowMap);
        }

        AnalysisResult result = new AnalysisResult();
        result.setRowCount(rows.size());
        result.setColumns(Arrays.asList(headers));
        result.setNumericColumns(numericColumns);
        result.setCategoryColumn(categoryColumn);
        result.setStats(stats);
        result.setCategoryBreakdown(categoryBreakdown);
        result.setPreview(preview);
        return result;
    }

    private int indexOf(String[] arr, String value) {
        for (int i = 0; i < arr.length; i++) if (arr[i].equals(value)) return i;
        return -1;
    }

    private boolean isParsableNumber(String s) {
        if (s == null || s.isBlank()) return false;
        try {
            Double.parseDouble(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private double parseNumber(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    // Minimal CSV line splitter that handles simple quoted fields
    private String[] splitCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString().trim());
        return fields.toArray(new String[0]);
    }
}
