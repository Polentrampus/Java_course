package hotel.users.client;

import hotel.controller.export_import.CsvImporter;
import hotel.exception.csv.CsvImportLengthException;
import hotel.exception.csv.CsvImportParsingException;
import hotel.model.Hotel;
import hotel.model.service.Services;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ClientCsvImport implements CsvImporter<Client> {
    @Override
    public List<Client> importFromCsv(List<String[]> data) {
        List<Client> clients = new ArrayList<>();
        for (String[] row : data) {
            try {
                Client client = createEntity(row);
                clients.add(client);
            } catch (Exception e) {
                throw new CsvImportParsingException(row);
            }
        }
        return clients;
    }

    @Override
    public Client createEntity(String[] data) {
        if (data.length < 9) {
            throw new CsvImportLengthException(9, data.length);
        }
        Client client = new Client(
                Integer.parseInt(data[0]),
                data[1],
                data[2],
                data[3],
                LocalDate.parse(data[4]),
                parserServices(data[5]),
                Integer.parseInt(data[6]),
                LocalDate.parse(data[7]),
                LocalDate.parse(data[8])
        );
        return client;
    }

    @Override
    public String[] getHeader() {
        return new String[]{"id", "name", "surname", "patronymic", "dateOfBirth", "services", "idRoom", "checkOutDate", "departureDate"};
    }

    public List<Services> parserServices(String data) {
        if (data == null || data.trim().isEmpty() || data.equals("[]") || data.equals("0")) {
            return new ArrayList<>();
        }

        String cleanedData = data.replace("[", "").replace("]", "").replace("\"", "").trim();
        if (cleanedData.isEmpty()) {
            return new ArrayList<>();
        }
        List<Services> services = new ArrayList<>();
        return Arrays.stream(cleanedData.split(","))
                .map(String::trim)
                .filter(serviceName -> !serviceName.isEmpty())
                .map(serviceName -> Hotel.getInstance().getService(serviceName))
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}


