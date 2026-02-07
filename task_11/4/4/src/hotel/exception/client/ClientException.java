package hotel.exception.client;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class ClientException extends HotelException {
    private final static String decorator = "Ошибка клиента: ";

    public ClientException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ClientException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ClientException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

