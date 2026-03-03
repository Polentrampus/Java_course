package hotel.repository.employee;

import hotel.dao.EmployeeDAO;
import hotel.exception.SqlException;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcEmployeeRepository implements EmployeeRepository {
    private final EmployeeDAO employeeDao = EmployeeDAO.getInstance();

    @Override
    public Optional<Employee> findById(int id) {
        try {
            return employeeDao.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find employee by id", e);
        }
    }

    @Override
    public List<Employee> findAll() {
        try {
            return employeeDao.findAll();
        } catch (Throwable e) {
            throw new SqlException("Failed to get all employees", e);
        }
    }

    @Override
    public boolean save(Employee employee) {
        try {
            employeeDao.save(employee);
            return true;
        } catch (Throwable e) {
            throw new SqlException("Failed to save employee", e);
        }
    }

    @Override
    public boolean update(Employee employee) {
        try {
            employeeDao.update(employee);
            return true;
        } catch (Throwable e) {
            throw new SqlException("Failed to update employee", e);
        }
    }

    @Override
    public boolean delete(int id) {
        try {
            employeeDao.delete(id);
            return true;
        } catch (Throwable e) {
            throw new SqlException("Failed to delete employee", e);
        }
    }

    public List<Employee> findByPosition(EmployeeRole position) {
        try {
            return employeeDao.findByPosition(position);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find employees by position", e);
        }
    }

    @Override
    public List<Employee> findByRole(EmployeeRole role) throws SQLException {
        return employeeDao.findByPosition(role);
    }
}
