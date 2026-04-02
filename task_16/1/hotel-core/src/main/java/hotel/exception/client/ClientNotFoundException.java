package hotel.exception.client;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class ClientNotFoundException extends HotelException {
    public ClientNotFoundException(Integer clientId) {
        super(ErrorCode.CLIENT_NOT_FOUND,
                "Клиент с ID " + clientId + " не найден");
        addDetail("clientId", clientId);
    }
}