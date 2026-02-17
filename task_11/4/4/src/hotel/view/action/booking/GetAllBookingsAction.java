package hotel.view.action.booking;

import hotel.model.booking.Bookings;
import hotel.service.BookingService;
import hotel.service.ClientService;
import hotel.service.RoomService;
import hotel.view.action.BaseAction;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class GetAllBookingsAction extends BaseAction {
    private final BookingService bookingService;
    private final ClientService clientService;

    public GetAllBookingsAction(BookingService bookingService,
                                Scanner scanner,
                                ClientService clientService) {
        super(scanner);
        this.bookingService = bookingService;
        this.clientService = clientService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ВСЕ БРОНИРОВАНИЯ ===");

            List<Bookings> bookings = bookingService.getAllBookings();

            if (bookings.isEmpty()) {
                System.out.println("Бронирования не найдены.");
                return;
            }

            System.out.println("Всего бронирований: " + bookings.size());

            for (Bookings booking : bookings) {
                printBookingSummary(booking);
                System.out.println("================================");
            }

        } catch (Exception e) {
            System.out.println("Ошибка при получении списка бронирований: " + e.getMessage());
        }
    }

    private void printBookingSummary(Bookings booking) throws SQLException {
        System.out.println("ID: " + booking.getId() +
                " | Клиент: " + clientService.findById(booking.getClient()).get() + "." +
                " | Комната: №" + booking.getRoom() +
                " | Заезд: " + booking.getCheckInDate() +
                " | Выезд: " + booking.getCheckOutDate() +
                " | Статус: " + booking.getStatus() +
                " | Стоимость: $" + booking.getTotalPrice());
    }
}