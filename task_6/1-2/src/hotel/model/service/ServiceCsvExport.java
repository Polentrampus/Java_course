package hotel.model.service;

import hotel.controller.export_import.CsvExporter;

import java.util.ArrayList;
import java.util.List;

public class ServiceCsvExport implements CsvExporter<Services> {
    @Override
    public List<String[]> exportCSV(List<Services> services) {
        List<String[]> servicesList = new ArrayList<>();
        for (Services service : services) {
            String[] csvData = {
                    String.valueOf(service.getId()),
                    service.getName(),
                    service.getDescription(),
                    String.valueOf(service.getPrice())
            };
            servicesList.add(csvData);
        }
        return servicesList;
    }

    @Override
    public String[] getHeader() {
        return new String[] {"id", "name", "description", "price"};
    }
    @Override
    public String fileName() {
        return "servicesExport.csv";
    }
}
