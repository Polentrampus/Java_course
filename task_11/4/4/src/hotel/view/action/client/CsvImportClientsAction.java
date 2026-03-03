package hotel.view.action.client;

import hotel.service.ClientService;
import hotel.service.CsvImportService;
import hotel.model.users.client.Client;
import hotel.model.users.client.ClientCsvImport;
import hotel.view.action.BaseAction;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class CsvImportClientsAction extends BaseAction {
    private final ClientService clientService;
    private final CsvImportService importManager;

    public CsvImportClientsAction(ClientService clientService, Scanner scanner) {
        super(scanner);
        this.clientService = clientService;
        this.importManager = new CsvImportService();
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ИМПОРТ КЛИЕНТОВ ===");

            String filePath = readString("Введите путь к CSV файлу (например: exports/clientExport.csv): ");
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("Файл не найден: " + filePath);
                return;
            }

            System.out.println("Импорт из файла: " + file.getName());
            ClientCsvImport importer = new ClientCsvImport();
            List<Client> importedClients = importManager.importFromFile(file, importer);

            if (importedClients.isEmpty()) {
                System.out.println("Не удалось импортировать клиентов из файла");
                return;
            }

            System.out.println("Успешно импортировано клиентов: " + importedClients.size());

            int savedCount = 0;
            for (Client client : importedClients) {
                if (clientService.save(client)) {
                    savedCount++;
                }
            }

            System.out.println("Сохранено в базу данных: " + savedCount + " клиентов");

        } catch (Exception e) {
            System.out.println("Ошибка при импорте клиентов: " + e.getMessage());
        }
    }
}