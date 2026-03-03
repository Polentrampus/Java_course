package hotel.exception;

public class JsonSerializerException extends RuntimeException {
    public JsonSerializerException(Object obj) {
        super("Не удалось сериализовать объект " + obj.getClass().getSimpleName());
    }

    public JsonSerializerException(Object obj, String message) {
        super("Не удалось сериализовать объект " + obj.getClass().getSimpleName() + " " + message);
    }
}
