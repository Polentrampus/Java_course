package hotel.service;

import hotel.dto.CreateEmployeeRequest;
import hotel.exception.ErrorCode;
import hotel.exception.employee.EmployeeException;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.repository.employee.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeService {
    private EmployeeRepository employeeRepository;
    private EmployeeObserverService employeeObserverService;
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           EmployeeObserverService employeeObserverService) {
        this.employeeRepository = employeeRepository;
        this.employeeObserverService = employeeObserverService;
    }

    public EmployeeService() {
    }

    public void addPersonal(Collection<? extends Employee> persons) {
        log.info("addPersonal() with {} persons", persons.size());

        if (persons.isEmpty()) {
            log.warn("addPersonal(): No employees to add");
            System.out.println("Работников нет");
            throw new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND);
        }

        for (Employee person : persons) {
            employeeRepository.save(person);
            System.out.println("Добавили нового члена команды: " + person);
        }

        log.info("addPersonal(): added {} employees", persons.size());
    }

    public Optional<Employee> saveEmployee(CreateEmployeeRequest request) throws SQLException {
        log.info("saveEmployee()");
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setSurname(request.getSurname());
        employee.setPatronymic(request.getPatronymic());
        employee.setSalary(request.getSalary());
        employee.setPosition(request.getPosition());
        Integer employeeID = employeeRepository.save(employee);
        log.info("saveEmployee(): saved employee with id: {}", employee.getId());
        return getEmployeeById(employeeID);
    }

    public void deleteEmployee(int employeeId) throws SQLException {
        log.info("deleteEmployee() for id: {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            throw new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND);
        }
        employeeRepository.delete(employee);
        log.info("deleteEmployee(): deleted employee with id: {}", employeeId);
    }

    public void requestCleaning(int roomId) throws SQLException {
        log.info("requestCleaning() for room: {}", roomId);

        System.out.println("Запрос на уборку комнаты " + roomId);
        employeeObserverService.notifyCleaningRequest(roomId);
        log.info("requestCleaning(): requested cleaning for room: {}", roomId);
    }

    public void requestMaintenance(int roomId) throws SQLException {
        log.info("requestMaintenance() for room: {}", roomId);
        System.out.println("Запрос на обслуживание комнаты " + roomId);
        employeeObserverService.notifyMaintenanceRequest(roomId);
        log.info("requestMaintenance(): requested maintenance for room: {}", roomId);
    }

    public List<Employee> getAdmins() {
        log.info("getAdmins()");
        List<Employee> admins = employeeRepository.findAll().stream()
                .filter(e -> e.getPosition() == EmployeeRole.ADMIN)
                .toList();
        log.info("getAdmins(): found {} admins", admins.size());
        return admins;
    }

    public List<Employee> getMaids() {
        log.info("getMaids()");
        List<Employee> maids = employeeRepository.findAll().stream()
                .filter(e -> e.getPosition() == EmployeeRole.MAID)
                .toList();
        log.info("getMaids(): found {} maids", maids.size());
        return maids;
    }

    public List<Employee> getMenders() {
        log.info("getMenders()");
        List<Employee> menders = employeeRepository.findAll().stream()
                .filter(e -> e.getPosition() == EmployeeRole.MENDER)
                .toList();
        log.info("getMenders(): found {} menders", menders.size());
        return menders;
    }

    public List<Employee> findAll() {
        log.info("findAll()");
        List<Employee> employees = employeeRepository.findAll();
        log.info("findAll(): found {} employees", employees.size());
        return employees;
    }

    public Optional<Employee> getEmployeeById(Integer id) throws SQLException {
        log.info("getEmployeeById() for id: {}", id);
        Optional<Employee> employee = employeeRepository.findById(id);
        log.info("getEmployeeById(): found employee with id: {}", id);
        return employee;
    }

    public List<Employee> getEmployeesByPosition(EmployeeRole position) {
        log.info("getEmployeesByPosition() for position: {}", position);
        List<Employee> employees = employeeRepository.findByRole(position);
        log.info("getEmployeesByPosition(): found {} employees", employees.size());
        return employees;
    }
}