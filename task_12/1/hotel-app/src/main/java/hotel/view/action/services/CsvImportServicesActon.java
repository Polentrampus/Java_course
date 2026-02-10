package hotel.view.action.services;


import hotel.model.service.ServiceCsvImport;
import hotel.model.service.Services;
import hotel.service.CsvImportService;
import hotel.service.ServicesService;
import hotel.view.action.BaseAction;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class CsvImportServicesActon extends BaseAction {
    private final ServicesService servicesService;
    private final CsvImportService importManager;

    public CsvImportServicesActon(ServicesService servicesService, Scanner scanner) {
        super(scanner);
        this.servicesService = servicesService;
        this.importManager = new CsvImportService();
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ИМПОРТ УСЛУГ ===");

            String filePath = readString("Введите путь к CSV файлу: " +
                    "( C:\\my_program\\Java_course\\task_6\\1\\exports\\servicesExport.csv )");
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("Файл не найден: " + filePath);
                return;
            }

            System.out.println("Импорт из файла: " + file.getName());
            ServiceCsvImport importer = new ServiceCsvImport();
            List<Services> importedServices = importManager.importFromFile(file, importer);

            if (importedServices.isEmpty()) {
                System.out.println("Не удалось импортировать услуги из файла");
                return;
            }

            System.out.println("Успешно импортированы Услуги: " + importedServices.size());
            saveServiceToSystem(importedServices);
        } catch (Exception e) {
            System.out.println("Ошибка при импорте услуг: " + e.getMessage());
        }
    }

    public  void saveServiceToSystem(List<Services> importedServices) throws SQLException {
        for (Services service : importedServices) {
            servicesService.addService(service);
        }
    }
}
