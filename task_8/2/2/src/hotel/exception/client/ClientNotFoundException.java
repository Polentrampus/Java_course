package hotel.exception.client;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(int clientId) {
        super("Клиент с ID " + clientId + " не найден");
    }
}
