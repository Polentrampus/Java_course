package hotel.exception.employee;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class EmployeeException extends HotelException {
    private final static String decorator = "Ошибка работников: ";

    public EmployeeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EmployeeException(ErrorCode errorCode, String message) {
        super(errorCode, decorator+message);
    }

    public EmployeeException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, decorator+message, cause);
    }
}

