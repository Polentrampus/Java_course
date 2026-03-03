package hotel.exception.room;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class RoomException extends HotelException {
    private final static String decorator = "Ошибка комнат: ";

    public RoomException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RoomException(ErrorCode errorCode, String message) {
        super(errorCode, decorator+message);
    }

    public RoomException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, decorator+message, cause);
    }
}

