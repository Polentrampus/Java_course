package hotel.exception.booking;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class BookingNotFoundException extends HotelException {
    public BookingNotFoundException(int bookingID) {
        super(ErrorCode.BOOKING_NOT_FOUND,
                "Бронирование с ID " + bookingID + " не найдено");
        addDetail("bookingId", bookingID);
    }
}
