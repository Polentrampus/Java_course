package hotel.view.action.room;

import hotel.controller.AdminController;
import hotel.model.Hotel;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class GetInfoAboutRoomAction extends BaseAction {
    private final AdminController adminController;

    public GetInfoAboutRoomAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ИНФОРМАЦИЯ О КОМНАТЕ ===");
            int roomNumber = readInt("Введите номер комнаты: ", 0, 9999);
            adminController.getInfoAboutRoom(roomNumber);
        } catch (Exception e) {
            System.out.println("Ошибка при получении информации о комнате: " + e.getMessage());
        }
    }
}