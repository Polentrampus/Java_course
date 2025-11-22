package hotel.view.action.services;

import hotel.controller.AdminController;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class RequestListServicesAction extends BaseAction {
    private final AdminController adminController;

    public RequestListServicesAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== СПИСОК УСЛУГ ===");
            adminController.requestListServices();
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка услуг: " + e.getMessage());
        }
    }
}
