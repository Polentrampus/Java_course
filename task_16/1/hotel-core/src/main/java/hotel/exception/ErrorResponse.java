package hotel.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String code;
    private String message;
    private String details;
    private String path;
    private Map<String, Object> validationErrors;

    // Конструктор для простых ответов
    public ErrorResponse(String code, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.code = code;
        this.message = message;
        this.path = path;
    }

    // Конструктор с деталями
    public ErrorResponse(String code, String message, String details, String path) {
        this(code, message, path);
        this.details = details;
    }

    // Конструктор с validation ошибками
    public ErrorResponse(String code, String message, Map<String, Object> validationErrors, String path) {
        this(code, message, path);
        this.validationErrors = validationErrors;
    }
}