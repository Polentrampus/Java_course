package hotel.view.action.client;

import hotel.controller.AdminController;
import hotel.service.CsvExportService;
import hotel.model.filter.ClientFilter;
import hotel.model.users.client.Client;
import hotel.model.users.client.ClientCsvExport;
import hotel.view.action.BaseAction;

import java.util.List;
import java.util.Scanner;

public class CsvExportClientsAction extends BaseAction {
    private final AdminController adminController;
    private final CsvExportService csvExportService;

    public CsvExportClientsAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
        csvExportService = new CsvExportService();
    }

    @Override
    public void execute() {
        try {
            System.out.println("===ЭКСПОРТ КЛИЕНТОВ===");

            List<Client> clients = adminController.getInfoAboutClientDatabase(ClientFilter.ID).stream().toList();
            if (clients.isEmpty()) {
                System.out.println("Нет клиентов для экспорта!");
                return;
            }

            System.out.println("Найдены клиенты для экспорта: " + clients.size());

            ClientCsvExport exporter = new ClientCsvExport();
            csvExportService.exportToFile("exports", clients, exporter);

        } catch (Exception e) {
            System.out.println("Ошибка при экспорте клиентов: " + e.getMessage());
        }
    }
}
