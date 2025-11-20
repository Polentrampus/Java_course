package hotel.view.action.services;

import hotel.controller.AdminController;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class AddServiceAction extends BaseAction {
    private final AdminController adminController;

    public AddServiceAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ДОБАВЛЕНИЕ УСЛУГИ ===");
            String name = readString("Введите название услуги: ");
            String description = readString("Введите описание услуги: ");
            int price = readInt("Введите цену услуги: ", 0, 9999);

            adminController.addService(name, description, price);
            System.out.println("Услуга успешно добавлена!");

        } catch (Exception e) {
            System.out.println("Ошибка при добавлении услуги: " + e.getMessage());
        }
    }
}