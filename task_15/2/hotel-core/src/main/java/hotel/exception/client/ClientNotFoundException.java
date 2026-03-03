package hotel.exception.client;

import hotel.exception.ErrorCode;

public class ClientNotFoundException extends ClientException {
    public ClientNotFoundException(int clientId) {
        super(ErrorCode.CLIENT_NOT_FOUND,
                "Клиент с ID " + clientId + " не найден");
        addDetail("clientId", clientId);
    }
}
