package hotel.view.action.room;

import hotel.controller.AdminController;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class ChangeRoomPriceAction extends BaseAction {
    private final AdminController adminController;

    public ChangeRoomPriceAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ИЗМЕНЕНИЕ ЦЕНЫ КОМНАТЫ ===");
            int roomId = readInt("Введите ID комнаты: ");
            int newPrice = readInt("Введите новую цену: ");
            adminController.changeRoomPrice(roomId, newPrice);
            System.out.println("Цена комнаты успешно изменена!");
        } catch (Exception e) {
            System.out.println("Ошибка при изменении цены комнаты: " + e.getMessage());
        }
    }
}