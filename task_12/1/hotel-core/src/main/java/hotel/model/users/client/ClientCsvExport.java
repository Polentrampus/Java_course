package hotel.model.users.client;

import hotel.export_import.CsvExporter;

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
                    String.valueOf(client.getDateOfBirth()),
            };
            clientsList.add(csvData);
        }
        return clientsList;
    }

    @Override
    public String[] getHeader() {
        return new String[]{"id", "name", "surname", "patronymic", "dateOfBirth"};
    }

    @Override
    public String fileName() {
        return "clientExport.csv";
    }
}
