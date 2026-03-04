package hotel.exception.booking;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class BookingInvalidDatesException extends HotelException {
    public BookingInvalidDatesException(String message) {
        super(ErrorCode.BOOKING_INVALID_DATES, message);
    }
}