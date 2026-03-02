package hotel.repository.employee;

import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.repository.HotelRepository;

import java.util.List;

public interface EmployeeRepository extends HotelRepository<Employee> {
    List<Employee> findByRole(EmployeeRole role);
}
