package hotel.view.action.room;

import hotel.model.filter.RoomFilter;
import hotel.service.IRoomService;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class GetListAvailableRoomsAction extends BaseAction {
    private final IRoomService roomService;

    public GetListAvailableRoomsAction(IRoomService roomService, Scanner scanner) {
        super(scanner);
        this.roomService = roomService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== СПИСОК ДОСТУПНЫХ КОМНАТ ===");
            RoomFilter filter = readEnum(RoomFilter.class, "Выберите фильтр для поиска:");
            roomService.listAvailableRooms(filter);
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка комнат: " + e.getMessage());
        }
    }
}