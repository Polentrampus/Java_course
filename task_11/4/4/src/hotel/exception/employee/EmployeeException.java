package hotel.exception.employee;

public class EmployeeException extends RuntimeException {
    public EmployeeException(String message) {
        super("Ошибка работников"+message);
    }
}

