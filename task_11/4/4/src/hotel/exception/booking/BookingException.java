package hotel.exception.booking;

public class BookingException extends RuntimeException {
    public BookingException(String message) {
        super("Ошибка бронирований"+message);
    }
}

