package hotel.service;

import hotel.service.export_import.CsvImporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvImportService {
    public <T> List<T> importFromFile(File file, CsvImporter<T> importer) throws IOException {
        List<String[]> csvData = parseCsvFile(file);

        if (!csvData.isEmpty()) {
            String[] headers = csvData.get(0);
            csvData = csvData.subList(1, csvData.size());
        }

        return importer.importFromCsv(csvData);
    }

    public <T> List<T> importFromFile(String filePath, CsvImporter<T> importer) throws IOException {
        return importFromFile(new File(filePath), importer);
    }

    private List<String[]> parseCsvFile(File file) throws IOException {
        List<String[]> csvData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] row = parseCsvLine(line, ";");
                csvData.add(row);
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());;
        }

        return csvData;
    }

    private String[] parseCsvLine(String line, String delimiter) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == delimiter.charAt(0) && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }

        fields.add(currentField.toString().trim());

        return fields.toArray(new String[0]);
    }
}
