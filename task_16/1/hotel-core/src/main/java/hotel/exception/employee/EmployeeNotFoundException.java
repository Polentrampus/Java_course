package hotel.exception.employee;

import hotel.exception.ErrorCode;
import hotel.exception.client.ClientException;

public class EmployeeNotFoundException extends ClientException {
    public EmployeeNotFoundException(int employeeId) {
        super(ErrorCode.EMPLOYEE_NOT_FOUND,
                "Работник с ID " + employeeId + " не найден");
        addDetail("employeeId", employeeId);
    }
}
