package hotel.exception.booking;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class BookingException extends HotelException {
    private final static String decorator = "Ошибка бронирования: ";

    public BookingException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BookingException(ErrorCode errorCode, String message) {
        super(errorCode, decorator + message);
    }

    public BookingException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, decorator + message, cause);
    }
}

