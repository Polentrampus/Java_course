package hotel.exception;

import hotel.exception.booking.BookingException;
import hotel.exception.client.ClientException;
import hotel.exception.dao.DAOException;
import hotel.exception.employee.EmployeeException;
import hotel.exception.room.RoomAlreadyExistsException;
import hotel.exception.room.RoomException;
import hotel.exception.service.ServiceAlreadyExistsException;
import hotel.exception.service.ServiceException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            HotelException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(HotelException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("Resource not found - code: {}, path: {}, message: '{}'",
                errorCode.getCode(), request.getRequestURI(), ex.getMessage());

        return new ErrorResponse(
                errorCode.getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // ========== CONFLICT (409) ==========

    @ExceptionHandler({
            RoomAlreadyExistsException.class,
            ServiceAlreadyExistsException.class,
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictExceptions(HotelException ex,
                                                  HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("Resource conflict - code: {}, path: {}, message: '{}'",
                errorCode.getCode(), request.getRequestURI(), ex.getMessage());

        return new ErrorResponse(
                errorCode.getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // ========== BAD REQUEST (400) ==========

    @ExceptionHandler({
            ValidationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestExceptions(RuntimeException ex,
                                                    HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        log.warn("Bad request - code: {}, path: {}, message: '{}'",
                errorCode.getCode(), request.getRequestURI(), ex.getMessage());

        return new ErrorResponse(
                errorCode.getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex,
                                                    HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        String errorDetails = errors.entrySet().stream()
                .map(e -> String.format("%s: %s", e.getKey(), e.getValue()))
                .collect(Collectors.joining("; "));

        log.warn("Validation error - code: {}, path: {}, errors: {}",
                ErrorCode.VALIDATION_ERROR.getCode(),
                request.getRequestURI(),
                errorDetails);

        return new ErrorResponse(
                ErrorCode.VALIDATION_ERROR.getCode(),
                "Ошибка валидации входных данных",
                errors.toString(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex,
                                                            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                )
        );

        log.warn("Constraint violation - code: {}, path: {}, errors: {}",
                ErrorCode.VALIDATION_ERROR.getCode(),
                request.getRequestURI(),
                errors);

        return new ErrorResponse(
                ErrorCode.VALIDATION_ERROR.getCode(),
                "Ошибка валидации данных",
                errors.toString(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String expectedType = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "неизвестный тип";

        String message = String.format("Параметр '%s' имеет неверный тип. Ожидается: %s",
                ex.getName(), expectedType);

        log.warn("Argument type mismatch - code: {}, path: {}, message: '{}'",
                ErrorCode.VALIDATION_ERROR.getCode(),
                request.getRequestURI(),
                message);

        return new ErrorResponse(
                ErrorCode.VALIDATION_ERROR.getCode(),
                message,
                request.getRequestURI()
        );
    }

    // ========== INTERNAL SERVER ERROR (500) ==========

    @ExceptionHandler(DAOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDaoException(DAOException ex,
                                            HttpServletRequest request) {
        log.error("Database error - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage(),
                ex);

        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                "Ошибка при работе с базой данных",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler({
            BookingException.class,
            ClientException.class,
            RoomException.class,
            EmployeeException.class,
            ServiceException.class,
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleBusinessExceptions(HotelException ex,
                                                  HttpServletRequest request) {
        log.error("Business error - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage(),
                ex);

        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                "Ошибка обработки запроса",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex,
                                                HttpServletRequest request) {
        log.error("Unexpected error - code: {}, path: {}, message: '{}'",
                ErrorCode.UNEXPECTED_ERROR.getCode(),
                request.getRequestURI(),
                ex.getMessage(),
                ex);

        return new ErrorResponse(
                ErrorCode.UNEXPECTED_ERROR.getCode(),
                "Внутренняя ошибка сервера",
                "Произошла непредвиденная ошибка. Пожалуйста, обратитесь к администратору.",
                request.getRequestURI()
        );
    }
}