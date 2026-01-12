package hotel.view.action.client;

import hotel.controller.AdminController;
import hotel.model.filter.ClientFilter;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class GetInfoAboutClientDatabaseAction extends BaseAction {
    private final AdminController adminController;

    public GetInfoAboutClientDatabaseAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== БАЗА ДАННЫХ КЛИЕНТОВ ===");
            ClientFilter filter = readEnum(ClientFilter.class, "Выберите фильтр клиентов:");
            adminController.getInfoAboutClientDatabase(filter);
        } catch (Exception e) {
            System.out.println("Ошибка при получении информации о клиентах: " + e.getMessage());
        }
    }
}