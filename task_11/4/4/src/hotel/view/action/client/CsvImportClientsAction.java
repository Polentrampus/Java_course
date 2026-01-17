package hotel.view.action.client;

import hotel.controller.AdminController;
import hotel.model.Hotel;
import hotel.service.CsvImportService;
import hotel.model.filter.RoomFilter;
import hotel.model.users.client.Client;
import hotel.model.users.client.ClientCsvImport;
import hotel.view.action.BaseAction;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class CsvImportClientsAction extends BaseAction {
    private final AdminController adminController;
    private final CsvImportService importManager;

    public CsvImportClientsAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
        this.importManager = new CsvImportService();
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ИМПОРТ КЛИЕНТОВ ===");

            String filePath = readString("Введите путь к CSV файлу: " +
                    "( C:\\my_program\\Java_course\\task_6\\1\\exports\\clientExport.csv )");
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
            saveClientToSystem(importedClients);
        } catch (Exception e) {
            System.out.println("Ошибка при импорте клиентов: " + e.getMessage());
        }
    }

    public  void saveClientToSystem(List<Client> importedClients) {
        for (Client client : importedClients) {
            Hotel.getInstance().getClientMap().get().put(client.getId(), client);
        }
    }
}
