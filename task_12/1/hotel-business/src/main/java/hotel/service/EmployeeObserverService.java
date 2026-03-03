package hotel.service;

import hotel.annotation.Component;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.repository.employee.EmployeeRepository;

import java.sql.SQLException;
import java.util.List;

@Component
public class EmployeeObserverService {
    private EmployeeRepository employeeRepository;

    public EmployeeObserverService() {
    }

    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void notifyObservers(EmployeeRole role, int roomId, String requestType) throws SQLException {
        List<Employee> employees = employeeRepository.findByRole(role);
        if (employees != null && !employees.isEmpty()) {
            Employee employee = employees.get(0);
            employee.handleRequest(roomId, requestType);
        } else {
            System.out.printf("Нет доступных сотрудников для роли %s\n",
                    role.getDisplayName());
        }
    }

    public void notifyCleaningRequest(int roomId) throws SQLException {
        notifyObservers(EmployeeRole.MAID, roomId, "CLEANING");
    }

    public void notifyRepairRequest(int roomId) throws SQLException {
        notifyObservers(EmployeeRole.MENDER, roomId, "REPAIR");
    }

    public void notifyMaintenanceRequest(int roomId) throws SQLException {
        notifyObservers(EmployeeRole.MENDER, roomId, "MAINTENANCE");
    }

    public void notifyAdminRequest(int roomId, String requestType) throws SQLException {
        notifyObservers(EmployeeRole.ADMIN, roomId, requestType);
    }
}
