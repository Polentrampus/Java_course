package hotel.exception.dao;


import hotel.exception.ErrorCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class DAOException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> details;
    private final String userMessage;
    private Exception exception;

    public DAOException(ErrorCode errorCode) {
        this(errorCode, errorCode.getDescription(), null);
    }

    public DAOException(Exception exception, String userMessage) {
        this.userMessage = userMessage;
        this.exception = exception;
        this.details = new HashMap<>();
        this.errorCode = null;
    }

    public DAOException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    public DAOException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = new HashMap<>();
        this.userMessage = message;
    }

    public DAOException addDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }

}
