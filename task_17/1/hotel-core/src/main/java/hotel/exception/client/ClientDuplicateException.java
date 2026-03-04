package hotel.exception.client;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class ClientDuplicateException extends HotelException {
    public ClientDuplicateException(String field, String value) {
        super(ErrorCode.CLIENT_DUPLICATE,
                "Клиент с таким " + field + " уже существует: " + value);
        addDetail("field", field);
        addDetail("value", value);
    }
}
