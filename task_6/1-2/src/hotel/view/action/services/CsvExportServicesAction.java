package hotel.view.action.services;

import hotel.controller.AdminController;
import hotel.controller.manager.CsvExportManager;
import hotel.model.service.ServiceCsvExport;
import hotel.model.service.Services;
import hotel.view.action.BaseAction;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class CsvExportServicesAction extends BaseAction {
    private AdminController adminController;
    private CsvExportManager csvExportManager;

    public CsvExportServicesAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.csvExportManager = new CsvExportManager();
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ЭКСПОРТ УСЛУГ ===");

            Collection<Services> services = adminController.requestListServices();

            if (services.isEmpty()) {
                System.out.println("Нет услуг для экспорта!");
                return;
            }

            System.out.println("Найдено услуг для экспорта: " + services.size());
            ServiceCsvExport exporter = new ServiceCsvExport();
            csvExportManager.exportToFile("exports", (List<Services>) services, exporter);

        } catch (Exception e) {
            System.out.println("Ошибка при экспорте услуг: " + e.getMessage());
        }
    }
}
