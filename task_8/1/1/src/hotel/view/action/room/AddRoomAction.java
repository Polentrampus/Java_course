package hotel.view.action.room;
import hotel.controller.AdminController;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class AddRoomAction extends BaseAction {
    private final AdminController adminController;

    public AddRoomAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== Добавление комнаты ===");

            int roomNumber = readInt("Введите номер комнаты: ");
            RoomType roomType = readEnum(RoomType.class, "Введите тип комнаты");
            int price = readInt("Введите цену: ");
            RoomCategory roomCategory = readEnum(RoomCategory.class, "Введите категорию комнаты");
            int capacity = readInt("Введите вместительность комнаты: ");

            adminController.addRoom(roomCategory, RoomStatus.AVAILABLE, roomType, roomNumber, price, capacity);
            System.out.println("Комната успешно добавлена!");

        } catch (Exception e) {
            System.out.println("Ошибка при добавлении комнаты: " + e.getMessage());
        }
    }
}