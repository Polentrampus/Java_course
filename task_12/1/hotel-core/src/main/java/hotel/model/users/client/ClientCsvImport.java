package hotel.model.users.client;

import hotel.export_import.CsvImporter;
import hotel.exception.csv.CsvImportLengthException;
import hotel.exception.csv.CsvImportParsingException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
                data[5]
        );
        return client;
    }

    @Override
    public String[] getHeader() {
        return new String[]{"id", "name", "surname", "patronymic", "dateOfBirth", "notes"};
    }
}


