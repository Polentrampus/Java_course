package hotel.controller.manager;

import hotel.controller.export_import.CsvExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExportManager {

    public <T> void exportToFile(File file, List<T> entities, CsvExporter<T> exporter) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {

            writer.write(convertToCsvLine(exporter.getHeader()));
            writer.println();

            List<String[]> csvData = exporter.exportCSV(entities);
            for (String[] row : csvData) {
                writer.write(convertToCsvLine(row));
                writer.println();
            }

            System.out.println("Экспортировано " + csvData.size() + " записей в: " + file.getAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException("Ошибка экспорта: " + e.getMessage(), e);
        }
    }

    public <T> void exportToFile(String directoryPath, List<T> entities, CsvExporter<T> exporter) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, exporter.fileName());
        exportToFile(file, entities, exporter);
    }

    private String convertToCsvLine(String[] fields) {
        StringBuilder csvLine = new StringBuilder();

        for (int i = 0; i < fields.length; i++) {
            if (i > 0) csvLine.append(";");
            csvLine.append(escapeCsvField(fields[i]));
        }

        return csvLine.toString();
    }

    private String escapeCsvField(String field) {
        if (field == null || field.isEmpty()) return "";

        boolean needsQuotes = field.contains(",") || field.contains("\"") ||
                field.contains("\n") || field.contains("\r");

        if (needsQuotes) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }
}