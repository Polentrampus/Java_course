package hotel.model.users.client;

import hotel.controller.export_import.CsvExporter;

import java.util.ArrayList;
import java.util.List;

public class ClientCsvExport implements CsvExporter<Client> {
    @Override
    public List<String[]> exportCSV(List<Client> clients) {
        List<String[]> clientsList = new ArrayList<>();

        for (Client client : clients) {
            String[] csvData = {
                    String.valueOf(client.getId()),
                    client.getName(),
                    client.getSurname(),
                    client.getPatronymic(),
                    String.valueOf(client.getDate_of_birth()),
                    String.valueOf(client.getServicesList()),
                    String.valueOf(client.getNumberRoom()),
                    String.valueOf(client.getCheckOutDate()),
                    String.valueOf(client.getDepartureDate())
            };
            clientsList.add(csvData);
        }
        return clientsList;
    }

    @Override
    public String[] getHeader() {
        return new String[]{"id", "name", "surname", "patronymic", "dateOfBirth", "services", "idRoom", "checkOutDate", "departureDate"};
    }

    @Override
    public String fileName() {
        return "clientExport.csv";
    }
}
