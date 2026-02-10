package hotel.repository.employee;

import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryEmployeeRepository implements EmployeeRepository {
    private Map<Integer, Employee> employeeMap = new HashMap<>();

    public InMemoryEmployeeRepository() {
        initializeTestData();
    }

    private void initializeTestData() {
        save(new Employee(1, "Иван", "Петров", "Сергеевич",
                LocalDate.of(1985, 5, 15), EmployeeRole.ADMIN, LocalDate.now(), 100000));
        save(new Employee(2, "Мария", "Иванова", "Александровна",
                LocalDate.of(1990, 8, 22), EmployeeRole.MAID, LocalDate.now(), 100000));
        save(new Employee(3, "Алексей", "Сидоров", "Викторович",
                LocalDate.of(1988, 3, 10), EmployeeRole.MENDER, LocalDate.now(), 100000));
        save(new Employee(4, "Елена", "Кузнецова", "Дмитриевна",
                LocalDate.of(1992, 11, 5), EmployeeRole.ADMIN, LocalDate.now(), 100000));
        save(new Employee(5, "Ольга", "Васильева", "Николаевна",
                LocalDate.of(1995, 7, 30), EmployeeRole.MAID, LocalDate.now(), 100000));
    }

    @Override
    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(employeeMap.get(id));
    }

    @Override
    public List<Employee> findAll() {
        return List.copyOf(employeeMap.values());
    }

    @Override
    public boolean save(Employee employee) {
        employeeMap.put(employee.getId(), employee);
        return true;
    }

    @Override
    public boolean update(Employee employee) {
        if (employeeMap.containsKey(employee.getId())) {
            employeeMap.put(employee.getId(), employee);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        return employeeMap.remove(id) != null;
    }

    @Override
    public List<Employee> findByRole(EmployeeRole role) {
        List<Employee> employees = new ArrayList<>();
        for (Employee employee : employeeMap.values()) {
            if (employee.getPosition() == role) {
                employees.add(employee);
            }
        }
        return employees;
    }
}