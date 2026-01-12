package hotel.view.action.services;

import hotel.controller.AdminController;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class ChangePriceServiceAction extends BaseAction {
    private final AdminController adminController;

    public ChangePriceServiceAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ИЗМЕНЕНИЕ ЦЕНЫ УСЛУГИ ===");
            String name = readString("Введите название услуги: ");
            int price = readInt("Введите новую цену: ", 0, 9999);

            adminController.changePriceService(name, price);
            System.out.println("Цена услуги успешно изменена!");

        } catch (Exception e) {
            System.out.println("Ошибка при изменении цены услуги: " + e.getMessage());
        }
    }
}