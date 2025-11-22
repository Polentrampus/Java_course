package hotel.model.service;

import hotel.controller.export_import.CsvImporter;
import hotel.exception.csv.CsvImportLengthException;
import hotel.exception.csv.CsvImportParsingException;

import java.util.ArrayList;
import java.util.List;

public class ServiceCsvImport implements CsvImporter<Services> {
    @Override
    public List<Services> importFromCsv(List<String[]> data) {
        List<Services> services = new ArrayList<>();
        for (String[] row : data) {
            try {
                Services service = createEntity(row);
                services.add(service);
            } catch (Exception e) {
                throw new CsvImportParsingException(row);
            }
        }
        return services;
    }

    @Override
    public Services createEntity(String[] data) {
        if (data.length < 4) {
            throw new CsvImportLengthException(4, data.length);
        }
        Services service = new Services(
                Integer.parseInt(data[0]),
                data[1],
                data[2],
                Double.parseDouble(data[3])
        );
        return service;
    }

    @Override
    public String[] getHeader() {
        return new String[] {"id", "name", "description", "price"};
    }

}
