package hotel.repository.employee;

import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.repository.person.PersonRepository;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeRepository extends PersonRepository<Employee> {
    List<Employee> findByRole(EmployeeRole role) throws SQLException;
}