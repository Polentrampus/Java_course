package hotel.exception.client;

public class ClientNotFoundException extends ClientException {
    public ClientNotFoundException(int clientId) {
        super("Клиент с ID " + clientId + " не найден");
    }
}
