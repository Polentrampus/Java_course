package hotel.exception;

import java.util.HashMap;
import java.util.Map;

public class HotelException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> details;
    private final String userMessage;

    public HotelException(ErrorCode errorCode) {
        this(errorCode, errorCode.getDescription(), null);
    }

    public HotelException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    public HotelException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = new HashMap<>();
        this.userMessage = message;
    }

    public HotelException addDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }

    // Getters
    public ErrorCode getErrorCode() { return errorCode; }
    public Map<String, Object> getDetails() { return details; }
    public String getUserMessage() { return userMessage; }

    public static HotelException clientNotFound(int clientId) {
        return new HotelException(ErrorCode.CLIENT_NOT_FOUND,
                "Клиент с ID " + clientId + " не найден")
                .addDetail("clientId", clientId);
    }

    public static HotelException roomNotFound(int roomId) {
        return new HotelException(ErrorCode.ROOM_NOT_FOUND,
                "Комната с ID " + roomId + " не найдена")
                .addDetail("roomId", roomId);
    }

    public static HotelException bookingNotFound(int bookingId) {
        return new HotelException(ErrorCode.BOOKING_NOT_FOUND,
                "Бронирование с ID " + bookingId + " не найдено")
                .addDetail("bookingId", bookingId);
    }

    public static HotelException serviceNotFound(String serviceName) {
        return new HotelException(ErrorCode.SERVICE_NOT_FOUND,
                "Услуга '" + serviceName + "' не найдена")
                .addDetail("serviceName", serviceName);
    }

    public static HotelException validationError(String field, String message) {
        return new HotelException(ErrorCode.VALIDATION_ERROR,
                "Ошибка валидации поля '" + field + "': " + message)
                .addDetail("field", field)
                .addDetail("validationMessage", message);
    }
}