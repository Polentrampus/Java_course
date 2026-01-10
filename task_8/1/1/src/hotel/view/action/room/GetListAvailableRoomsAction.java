package hotel.view.action.room;

import hotel.controller.AdminController;
import hotel.model.filter.RoomFilter;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class GetListAvailableRoomsAction extends BaseAction {
    private final AdminController adminController;

    public GetListAvailableRoomsAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== СПИСОК ДОСТУПНЫХ КОМНАТ ===");
            RoomFilter filter = readEnum(RoomFilter.class, "Выберите фильтр для поиска:");
            adminController.getListAvailableRooms(filter);
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка комнат: " + e.getMessage());
        }
    }
}