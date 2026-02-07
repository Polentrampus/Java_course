package hotel.exception.service;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class ServiceException extends HotelException {
    private final static String decorator = "Ошибка услуг: ";

    public ServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ServiceException(ErrorCode errorCode, String message) {
        super(errorCode, decorator+message);
    }

    public ServiceException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, decorator+message, cause);
    }
}
