package hotel.view.action.room;

import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.service.IRoomService;
import hotel.view.action.BaseAction;

import java.math.BigDecimal;
import java.util.Scanner;

public class AddRoomAction extends BaseAction {
    private final IRoomService roomService;

    public AddRoomAction(IRoomService roomService, Scanner scanner) {
        super(scanner);
        this.roomService = roomService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ДОБАВЛЕНИЕ НОВОЙ КОМНАТЫ ===");
            Room room = new Room();

            room.setNumber(readInt("Введите номер комнаты: "));
            room.setType(readEnum(RoomType.class, "Выберите тип комнаты (STANDARD, SUITE, DELUXE, PRESIDENTIAL):"));
            room.setCategory(readEnum(RoomCategory.class, "Выберите категорию комнаты (ECONOMY, COMFORT, BUSINESS, LUXURY):"));
            room.setCapacity(readInt("Введите вместимость комнаты (количество человек): "));
            room.setPrice(BigDecimal.valueOf(readInt("Введите цену за ночь ($): ")));
            room.setStatus(RoomStatus.AVAILABLE);

            System.out.println("========================================");
            System.out.println("Номер комнаты: " + room.getNumber());
            System.out.println("Тип: " + room.getType());
            System.out.println("Категория: " + room.getCategory());
            System.out.println("Вместимость: " + room.getCapacity() + " чел.");
            System.out.println("Цена за ночь: $" + room.getPrice());
            System.out.println("========================================");

            roomService.save(room);

            System.out.println("\nКомната успешно добавлена!");

        } catch (Exception e) {
            System.out.println("Ошибка при добавлении комнаты: " + e.getMessage());
        }
    }
}