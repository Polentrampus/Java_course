package hotel.exception.booking;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class BookingNotFoundException extends HotelException {
    public BookingNotFoundException(Integer bookingId) {
        super(ErrorCode.BOOKING_NOT_FOUND,
                "Бронирование с ID " + bookingId + " не найдено");
        addDetail("bookingId", bookingId);
    }
}
