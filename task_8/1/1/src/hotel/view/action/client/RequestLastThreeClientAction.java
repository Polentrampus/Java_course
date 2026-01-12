package hotel.view.action.client;

import hotel.controller.AdminController;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class RequestLastThreeClientAction extends BaseAction {
    private final AdminController adminController;

    public RequestLastThreeClientAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ПОСЛЕДНИЕ 3 КЛИЕНТА ===");
            adminController.requestLastThreeClient();
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка клиентов: " + e.getMessage());
        }
    }
}