package hotel.exception.booking;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class BookingCreationException extends HotelException {
    public BookingCreationException(String message, Throwable cause) {
        super(ErrorCode.BOOKING, message, cause);
    }
}
