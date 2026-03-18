package hotel.exception.employee;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class EmployeeInvalidRoleException extends HotelException {
    public EmployeeInvalidRoleException(String role) {
        super(ErrorCode.EMPLOYEE_INVALID_ROLE,
                "Некорректная роль сотрудника: " + role);
        addDetail("role", role);
    }
}