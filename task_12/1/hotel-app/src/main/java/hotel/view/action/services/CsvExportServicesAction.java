package hotel.view.action.services;


import hotel.model.service.ServiceCsvExport;
import hotel.model.service.Services;
import hotel.service.CsvExportService;
import hotel.service.ServicesService;
import hotel.view.action.BaseAction;

import java.util.List;
import java.util.Scanner;

public class CsvExportServicesAction extends BaseAction {
    private ServicesService serviceService;
    private CsvExportService csvExportService;

    public CsvExportServicesAction(ServicesService serviceService, Scanner scanner) {
        super(scanner);
        this.csvExportService = new CsvExportService();
        this.serviceService = serviceService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ЭКСПОРТ УСЛУГ ===");

            List<Services> services = serviceService.findAll();

            if (services.isEmpty()) {
                System.out.println("Нет услуг для экспорта!");
                return;
            }

            System.out.println("Найдено услуг для экспорта: " + services.size());
            ServiceCsvExport exporter = new ServiceCsvExport();
            csvExportService.exportToFile("exports", services, exporter);
        } catch (Exception e) {
            System.out.println("Ошибка при экспорте услуг: " + e.getMessage());
        }
    }
}
