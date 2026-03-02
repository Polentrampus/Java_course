package hotel.exception;

public class ConfigException extends RuntimeException {
    public ConfigException() {
        super("Изменение запрещено конфигурацией");
    }
}
