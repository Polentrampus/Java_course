package hotel.view.action.room;

import hotel.controller.AdminController;
import hotel.model.filter.RoomFilter;
import hotel.view.action.BaseAction;

import java.time.LocalDate;
import java.util.Scanner;

public class GetListAvailableRoomsByDateAction extends BaseAction {
    private final AdminController adminController;

    public GetListAvailableRoomsByDateAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== СПИСОК КОМНАТ ПО ДАТЕ ===");
            RoomFilter filter = readEnum(RoomFilter.class, "Выберите фильтр:");
            LocalDate date = readDate("Введите дату для проверки доступности");
            adminController.getListAvailableRoomsByDate(filter, date);
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка комнат: " + e.getMessage());
        }
    }
}