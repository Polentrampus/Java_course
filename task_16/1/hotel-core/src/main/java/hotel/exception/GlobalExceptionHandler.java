package hotel.exception;

import hotel.exception.booking.BookingException;
import hotel.exception.booking.BookingNotFoundException;
import hotel.exception.client.ClientException;
import hotel.exception.client.ClientNotFoundException;
import hotel.exception.employee.EmployeeException;
import hotel.exception.employee.EmployeeNotFoundException;
import hotel.exception.room.RoomAlreadyExistsException;
import hotel.exception.room.RoomException;
import hotel.exception.room.RoomNotFoundException;
import hotel.exception.service.ServiceAlreadyExistsException;
import hotel.exception.service.ServiceException;
import hotel.exception.service.ServiceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
//
//    @ExceptionHandler({
//            ClientNotFoundException.class,
//            RoomNotFoundException.class,
//            BookingNotFoundException.class,
//            EmployeeNotFoundException.class,
//            ServiceNotFoundException.class
//    })
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleNotFoundExceptions(RuntimeException ex,
//                                                  GenericServletWrapper.HttpServletRequest request) {
//        ErrorCode errorCode = extractErrorCode(ex);
//        log.warn("Resource not found - code: {}, path: {}, message: '{}'",
//                errorCode.getCode(), request.getRequestURI(), ex.getMessage());
//
//        return new ErrorResponse(
//                errorCode.getCode(),
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//    }
//
//    // ========== CONFLICT (409) ==========
//
//    @ExceptionHandler({
//            RoomAlreadyExistsException.class,
//            ServiceAlreadyExistsException.class,
//            hotel.exception.DuplicateEntityException.class
//    })
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ErrorResponse handleConflictExceptions(RuntimeException ex,
//                                                  GenericServletWrapper.HttpServletRequest request) {
//        ErrorCode errorCode = extractErrorCode(ex);
//        log.warn("Resource conflict - code: {}, path: {}, message: '{}'",
//                errorCode.getCode(), request.getRequestURI(), ex.getMessage());
//
//        return new ErrorResponse(
//                errorCode.getCode(),
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//    }
//
//    @ExceptionHandler(ResourceBusyException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ErrorResponse handleResourceBusyException(ResourceBusyException ex,
//                                                     HttpServletRequest request) {
//        log.warn("Resource busy - code: {}, path: {}, message: '{}'",
//                ex.getErrorCode().getCode(), request.getRequestURI(), ex.getMessage());
//
//        return new ErrorResponse(
//                ex.getErrorCode().getCode(),
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//    }
//
//    // ========== BAD REQUEST (400) ==========
//
//    @ExceptionHandler({
//            ValidationException.class,
//            InvalidDateException.class,
//            OperationNotAllowedException.class,
//            ScheduleConflictException.class
//    })
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleBadRequestExceptions(RuntimeException ex,
//                                                    HttpServletRequest request) {
//        ErrorCode errorCode = extractErrorCode(ex);
//        log.warn("Bad request - code: {}, path: {}, message: '{}'",
//                errorCode.getCode(), request.getRequestURI(), ex.getMessage());
//
//        return new ErrorResponse(
//                errorCode.getCode(),
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex,
//                                                    HttpServletRequest request) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage())
//        );
//
//        String errorDetails = errors.entrySet().stream()
//                .map(e -> String.format("%s: %s", e.getKey(), e.getValue()))
//                .collect(Collectors.joining("; "));
//
//        log.warn("Validation error - code: {}, path: {}, errors: {}",
//                ErrorCode.VALIDATION_ERROR.getCode(),
//                request.getRequestURI(),
//                errorDetails);
//
//        return new ErrorResponse(
//                ErrorCode.VALIDATION_ERROR.getCode(),
//                "Ошибка валидации входных данных",
//                errors,
//                request.getRequestURI()
//        );
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex,
//                                                            HttpServletRequest request) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getConstraintViolations().forEach(violation ->
//                errors.put(
//                        violation.getPropertyPath().toString(),
//                        violation.getMessage()
//                )
//        );
//
//        log.warn("Constraint violation - code: {}, path: {}, errors: {}",
//                ErrorCode.VALIDATION_ERROR.getCode(),
//                request.getRequestURI(),
//                errors);
//
//        return new ErrorResponse(
//                ErrorCode.VALIDATION_ERROR.getCode(),
//                "Ошибка валидации данных",
//                errors,
//                request.getRequestURI()
//        );
//    }
//
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleMethodArgumentTypeMismatchException(
//            MethodArgumentTypeMismatchException ex,
//            HttpServletRequest request) {
//
//        String expectedType = ex.getRequiredType() != null
//                ? ex.getRequiredType().getSimpleName()
//                : "неизвестный тип";
//
//        String message = String.format("Параметр '%s' имеет неверный тип. Ожидается: %s",
//                ex.getName(), expectedType);
//
//        log.warn("Argument type mismatch - code: {}, path: {}, message: '{}'",
//                ErrorCode.VALIDATION_ERROR.getCode(),
//                request.getRequestURI(),
//                message);
//
//        return new ErrorResponse(
//                ErrorCode.VALIDATION_ERROR.getCode(),
//                message,
//                request.getRequestURI()
//        );
//    }
//
//    // ========== INTERNAL SERVER ERROR (500) ==========
//
//    @ExceptionHandler(DaoException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleDaoException(DaoException ex,
//                                            HttpServletRequest request) {
//        log.error("Database error - code: {}, path: {}, message: '{}'",
//                ex.getErrorCode().getCode(),
//                request.getRequestURI(),
//                ex.getMessage(),
//                ex);
//
//        return new ErrorResponse(
//                ex.getErrorCode().getCode(),
//                "Ошибка при работе с базой данных",
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//    }
//
//    @ExceptionHandler({
//            BookingException.class,
//            ClientException.class,
//            RoomException.class,
//            EmployeeException.class,
//            ServiceException.class,
//            CsvImportException.class
//    })
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleBusinessExceptions(HotelException ex,
//                                                  HttpServletRequest request) {
//        log.error("Business error - code: {}, path: {}, message: '{}'",
//                ex.getErrorCode().getCode(),
//                request.getRequestURI(),
//                ex.getMessage(),
//                ex);
//
//        return new ErrorResponse(
//                ex.getErrorCode().getCode(),
//                "Ошибка обработки запроса",
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//    }
//
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleGenericException(Exception ex,
//                                                HttpServletRequest request) {
//        log.error("Unexpected error - code: {}, path: {}, message: '{}'",
//                ErrorCode.UNEXPECTED_ERROR.getCode(),
//                request.getRequestURI(),
//                ex.getMessage(),
//                ex);
//
//        return new ErrorResponse(
//                ErrorCode.UNEXPECTED_ERROR.getCode(),
//                "Внутренняя ошибка сервера",
//                "Произошла непредвиденная ошибка. Пожалуйста, обратитесь к администратору.",
//                request.getRequestURI()
//        );
//    }
//
//    // Вспомогательный метод для извлечения ErrorCode из исключений
//    private ErrorCode extractErrorCode(RuntimeException ex) {
//        if (ex instanceof HotelException) {
//            return ((HotelException) ex).getErrorCode();
//        }
//        // Дефолтные значения для стандартных исключений
//        if (ex instanceof ClientNotFoundException) return ErrorCode.CLIENT_NOT_FOUND;
//        if (ex instanceof RoomNotFoundException) return ErrorCode.ROOM_NOT_FOUND;
//        if (ex instanceof BookingNotFoundException) return ErrorCode.BOOKING_NOT_FOUND;
//        if (ex instanceof EmployeeNotFoundException) return ErrorCode.EMPLOYEE_NOT_FOUND;
//        if (ex instanceof ServiceNotFoundException) return ErrorCode.SERVICE_NOT_FOUND;
//        if (ex instanceof RoomAlreadyExistsException) return ErrorCode.ROOM_ALREADY_EXISTS;
//        if (ex instanceof ServiceAlreadyExistsException) return ErrorCode.SERVICE_ALREADY_EXISTS;
//        if (ex instanceof IncorrectDateEntryException) return ErrorCode.BOOKING_INVALID_DATES;
//        if (ex instanceof ValidationException) return ErrorCode.VALIDATION_ERROR;
//
//        return ErrorCode.UNEXPECTED_ERROR;
//    }
}