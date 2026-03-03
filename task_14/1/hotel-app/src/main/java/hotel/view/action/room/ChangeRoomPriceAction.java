package hotel.view.action.room;

import hotel.service.IRoomService;
import hotel.view.action.BaseAction;

import java.math.BigDecimal;
import java.util.Scanner;

public class ChangeRoomPriceAction extends BaseAction {
    private final IRoomService roomService;

    public ChangeRoomPriceAction(IRoomService roomService, Scanner scanner) {
        super(scanner);
        this.roomService = roomService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ИЗМЕНЕНИЕ ЦЕНЫ КОМНАТЫ ===");
            int roomId = readInt("Введите ID комнаты: ");
            BigDecimal newPrice = BigDecimal.valueOf(readInt("Введите новую цену: "));
            roomService.setTotalPrice(roomId, newPrice);
            System.out.println("Цена комнаты успешно изменена!");
        } catch (Exception e) {
            System.out.println("Ошибка при изменении цены комнаты: " + e.getMessage());
        }
    }
}