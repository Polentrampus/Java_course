package hotel.service;

import hotel.annotation.Component;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.repository.employee.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
public class EmployeeService {
    private EmployeeRepository employeeRepository;
    private final EmployeeObserverService employeeObserverService = new EmployeeObserverService();
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    public void setHotelRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        employeeObserverService.setEmployeeRepository(employeeRepository);
    }

    public EmployeeService() {
    }

    public void addPersonal(Collection<? extends Employee> persons) {
        log.info("addPersonal()");
        if (persons.isEmpty()) {
            log.warn("addPersonal(): Работников нет");
            System.out.println("Работников нет");
            return;
        }
        for (Employee person : persons) {
            employeeRepository.save(person);
            System.out.println("Добавили нового члена команды: " + person.toString());
        }
        log.info("addPersonal(): added " + persons.size() + " employees");
    }

    public void saveEmployee(Employee employee) {
        log.info("saveEmployee()");
        employeeRepository.save(employee);
        log.info("saveEmployee(): saved employee: " + employee);
    }

    public void deleteEmployee(int employeeId) {
        log.info("deleteEmployee()");
        employeeRepository.delete(employeeId);
        log.info("deleteEmployee(): deleted employee with id: " + employeeId);
    }

    public void requestCleaning(int roomId) throws SQLException {
        log.info("requestCleaning()");
        System.out.println("Запрос на уборку комнаты " + roomId);
        employeeObserverService.notifyCleaningRequest(roomId);
        log.info("requestCleaning(): requested cleaning for room: " + roomId);
    }

    public void requestRepair(int roomId) throws SQLException {
        log.info("requestRepair()");
        System.out.println("Запрос на ремонт комнаты " + roomId);
        employeeObserverService.notifyRepairRequest(roomId);
        log.info("requestRepair(): requested repair for room: " + roomId);
    }

    public void requestMaintenance(int roomId) throws SQLException {
        log.info("requestMaintenance()");
        System.out.println("Запрос на обслуживание комнаты " + roomId);
        employeeObserverService.notifyMaintenanceRequest(roomId);
        log.info("requestMaintenance(): requested maintenance for room: " + roomId);
    }

    public List<Employee> getAdmins() {
        log.info("getAdmins()");
        List<Employee> admins = employeeRepository.findAll().stream()
                .filter(e -> e.getPosition() == EmployeeRole.ADMIN)
                .toList();
        log.info("getAdmins(): admins size: " + admins.size());
        return admins;
    }

    public List<Employee> getMaids() {
        log.info("getMaids()");
        List<Employee> maids = employeeRepository.findAll().stream()
                .filter(e -> e.getPosition() == EmployeeRole.MAID)
                .toList();
        log.info("getMaids(): maids size: " + maids.size());
        return maids;
    }

    public List<Employee> getMenders() {
        log.info("getMenders()");
        List<Employee> menders = employeeRepository.findAll().stream()
                .filter(e -> e.getPosition() == EmployeeRole.MENDER)
                .toList();
        log.info("getMenders(): menders size: " + menders.size());
        return menders;
    }

    public List<Employee> findAll() {
        log.info("findAll()");
        List<Employee> employees = employeeRepository.findAll();
        log.info("findAll(): employees size: " + employees.size());
        return employees;
    }
}