package hotel.exception.employee;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class EmployeeNotFoundException extends HotelException {
    public EmployeeNotFoundException(Integer employeeId) {
        super(ErrorCode.EMPLOYEE_NOT_FOUND,
                "Сотрудник с ID " + employeeId + " не найден");
        addDetail("employeeId", employeeId);
    }
}