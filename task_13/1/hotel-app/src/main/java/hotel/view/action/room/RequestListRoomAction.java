package hotel.view.action.room;

import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.service.IRoomService;
import hotel.view.action.BaseAction;

import java.util.List;
import java.util.Scanner;

public class RequestListRoomAction extends BaseAction {
    private final IRoomService roomService;

    public RequestListRoomAction(IRoomService roomService, Scanner scanner) {
        super(scanner);
        this.roomService = roomService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== СПИСОК ВСЕХ КОМНАТ ===");
            RoomFilter filter = readEnum(RoomFilter.class, "Выберите фильтр:");
            List<Room> rooms = roomService.findAll();
            rooms.sort(filter.getComparator());
            for (Room room : rooms) {
                System.out.println(room);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка комнат: " + e.getMessage());
        }
    }
}