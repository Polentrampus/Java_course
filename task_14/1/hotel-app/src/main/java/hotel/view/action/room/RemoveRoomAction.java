package hotel.view.action.room;

import hotel.exception.room.RoomNotFoundException;
import hotel.model.room.Room;
import hotel.service.IRoomService;
import hotel.view.action.BaseAction;

import java.util.Optional;
import java.util.Scanner;

public class RemoveRoomAction extends BaseAction {
    private final IRoomService roomService;

    public RemoveRoomAction(IRoomService roomService, Scanner scanner) {
        super(scanner);
        this.roomService = roomService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== УДАЛЕНИЕ КОМНАТЫ ===");
            Integer roomId = readInt("Введите номер комнаты: ");
            Optional<Room> room = roomService.findById(roomId);

            if (room.isEmpty()) {
                throw new RoomNotFoundException(roomId);
            }

            System.out.println("\nВы действительно хотите удалить это бронирование?");
            System.out.println("========================================");
            System.out.println("Номер комнаты: " + room.get().getNumber());
            System.out.println("Тип: " + room.get().getType());
            System.out.println("Категория: " + room.get().getCategory());
            System.out.println("Вместимость: " + room.get().getCapacity() + " чел.");
            System.out.println("Цена за ночь: $" + room.get().getPrice());
            System.out.println("========================================");

            String confirm = readString("Введите 'yes' для подтверждения удаления: ");
            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("Удаление отменено.");
                return;
            }
            roomService.delete(room.get());

            System.out.println("\nКомната успешно удалена!");

        } catch (Exception e) {
            System.out.println("Ошибка при удалении комнаты: " + e.getMessage());
        }
    }
}
