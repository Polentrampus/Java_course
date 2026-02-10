package hotel.view.action.room;

import hotel.model.filter.RoomFilter;
import hotel.service.IRoomService;
import hotel.view.action.BaseAction;

import java.time.LocalDate;
import java.util.Scanner;

public class GetListAvailableRoomsByDateAction extends BaseAction {
    private final IRoomService roomService;

    public GetListAvailableRoomsByDateAction(IRoomService roomService, Scanner scanner) {
        super(scanner);
        this.roomService = roomService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== СПИСОК КОМНАТ ПО ДАТЕ ===");
            RoomFilter filter = readEnum(RoomFilter.class, "Выберите фильтр:");
            LocalDate date = readDate("Введите дату для проверки доступности");
            roomService.listAvailableRoomsByDate(filter, date);
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка комнат: " + e.getMessage());
        }
    }
}