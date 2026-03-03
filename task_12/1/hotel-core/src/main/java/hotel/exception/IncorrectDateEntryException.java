package hotel.exception;

public class IncorrectDateEntryException extends RuntimeException {
    public IncorrectDateEntryException(String message, Throwable cause) {
        super(message, cause);
    }
}
