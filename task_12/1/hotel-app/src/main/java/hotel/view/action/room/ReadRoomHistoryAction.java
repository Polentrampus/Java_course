package hotel.view.action.room;

import hotel.model.booking.Bookings;
import hotel.service.IBookingService;
import hotel.view.action.BaseAction;

import java.util.List;
import java.util.Scanner;

public class ReadRoomHistoryAction extends BaseAction {
    private final IBookingService bookingService;

    public ReadRoomHistoryAction(IBookingService bookingService, Scanner scanner) {
        super(scanner);
        this.bookingService = bookingService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== История комнаты ===");

            int roomNumber = readInt("Введите номер комнаты: ");

            System.out.println("История комнаты:");
            List<Bookings> bookings = bookingService.findByRoomId(roomNumber);
            for (Bookings booking : bookings) {
                System.out.println(booking.toString());
            }
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении комнаты: " + e.getMessage());
        }
    }
}
