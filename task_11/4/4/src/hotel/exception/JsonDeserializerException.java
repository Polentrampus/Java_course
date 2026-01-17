package hotel.exception;

public class JsonDeserializerException extends RuntimeException {
    public JsonDeserializerException(Object obj) {
        super("Не удалось десериализовать объект " + obj.getClass().getSimpleName());
    }

    public JsonDeserializerException(Object obj, String message) {
        super("Не удалось десериализовать объект " + obj.getClass().getSimpleName() + " " + message);
    }
}
