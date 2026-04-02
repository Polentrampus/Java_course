package hotel.view.action.room;

import hotel.service.IRoomService;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class GetInfoAboutRoomAction extends BaseAction {
    private final IRoomService roomService;

    public GetInfoAboutRoomAction(IRoomService roomService, Scanner scanner) {
        super(scanner);
        this.roomService = roomService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ИНФОРМАЦИЯ О КОМНАТЕ ===");
            int roomNumber = readInt("Введите номер комнаты: ", 0, 9999);
            System.out.println(roomService.findById(roomNumber).get());
        } catch (Exception e) {
            System.out.println("Ошибка при получении информации о комнате: " + e.getMessage());
        }
    }
}