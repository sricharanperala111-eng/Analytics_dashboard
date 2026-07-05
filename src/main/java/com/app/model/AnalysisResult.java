package com.app.model;

import java.util.List;
import java.util.Map;

public class AnalysisResult {

    private int rowCount;
    private List<String> columns;
    private List<String> numericColumns;
    private String categoryColumn;

    // column -> stats
    private Map<String, ColumnStats> stats;

    // category value -> aggregated sum of the primary numeric column
    private Map<String, Double> categoryBreakdown;

    // raw preview rows (first 10) for a table view
    private List<Map<String, String>> preview;

    public static class ColumnStats {
        public double sum;
        public double avg;
        public double min;
        public double max;

        public ColumnStats(double sum, double avg, double min, double max) {
            this.sum = sum; this.avg = avg; this.min = min; this.max = max;
        }
    }

    public int getRowCount() { return rowCount; }
    public void setRowCount(int rowCount) { this.rowCount = rowCount; }
    public List<String> getColumns() { return columns; }
    public void setColumns(List<String> columns) { this.columns = columns; }
    public List<String> getNumericColumns() { return numericColumns; }
    public void setNumericColumns(List<String> numericColumns) { this.numericColumns = numericColumns; }
    public String getCategoryColumn() { return categoryColumn; }
    public void setCategoryColumn(String categoryColumn) { this.categoryColumn = categoryColumn; }
    public Map<String, ColumnStats> getStats() { return stats; }
    public void setStats(Map<String, ColumnStats> stats) { this.stats = stats; }
    public Map<String, Double> getCategoryBreakdown() { return categoryBreakdown; }
    public void setCategoryBreakdown(Map<String, Double> categoryBreakdown) { this.categoryBreakdown = categoryBreakdown; }
    public List<Map<String, String>> getPreview() { return preview; }
    public void setPreview(List<Map<String, String>> preview) { this.preview = preview; }
}
