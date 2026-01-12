package hotel.view.action.services;

import hotel.controller.AdminController;
import hotel.service.CsvImportService;
import hotel.model.service.ServiceCsvImport;
import hotel.model.service.Services;
import hotel.view.action.BaseAction;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class CsvImportServicesActon extends BaseAction {
    private final AdminController adminController;
    private final CsvImportService importManager;

    public CsvImportServicesActon(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
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

    public  void saveServiceToSystem(List<Services> importedServices) {
        for (Services service : importedServices) {
            adminController.addService(
                    service.getId(),
                    service.getName(),
                    service.getDescription(),
                    (int) service.getPrice()
            );
        }
    }
}
