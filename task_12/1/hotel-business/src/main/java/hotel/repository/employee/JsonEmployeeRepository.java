package hotel.repository.employee;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.util.JsonDataManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonEmployeeRepository implements EmployeeRepository {
    private JsonDataManager  dataManager = JsonDataManager.getInstance();

    public JsonEmployeeRepository()  {
    }

    @Override
    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(dataManager.getEmployees().get(id));
    }

    @Override
    public List<Employee> findAll() {
        return List.copyOf(dataManager.getEmployees().values());
    }

    @Override
    public boolean save(Employee employee) {
        if (employee.getId() == null) {
            int maxId = dataManager.getEmployees().keySet().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);
            employee.setId(maxId + 1);
        }

        dataManager.saveEmployee(employee);
        return true;
    }

    @Override
    public boolean update(Employee employee) {
        dataManager.getEmployees().put(employee.getId(), employee);
        return true;
    }

    @Override
    public boolean delete(int id) {
        dataManager.deleteEmployee(id);
        return true;
    }

    @Override
    public List<Employee> findByRole(EmployeeRole role) throws SQLException {
        return dataManager.getEmployees().values().stream().
                filter(employee ->
                    employee.getPosition() == role).collect(Collectors.toList());
    }
}