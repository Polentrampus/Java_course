package hotel.service;

import hotel.annotation.Component;
import hotel.annotation.Inject;
import hotel.exception.ErrorCode;
import hotel.exception.employee.EmployeeException;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.repository.employee.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

@Component
public class EmployeeService {

    @Inject
    private EmployeeRepository employeeRepository;

    @Inject
    private EmployeeObserverService employeeObserverService;

    @Inject
    private TransactionManager transactionManager;

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    public EmployeeService() {
    }

    public void addPersonal(Collection<? extends Employee> persons) {
        log.info("addPersonal() with {} persons", persons.size());

        transactionManager.executeInTransaction(() -> {
            if (persons.isEmpty()) {
                log.warn("addPersonal(): No employees to add");
                System.out.println("Работников нет");
                return null;
            }

            for (Employee person : persons) {
                employeeRepository.save(person);
                System.out.println("Добавили нового члена команды: " + person);
            }

            log.info("addPersonal(): added {} employees", persons.size());
            return null;
        });
    }

    public void saveEmployee(Employee employee) {
        log.info("saveEmployee() for employee: {}", employee);

        transactionManager.executeInTransaction(() -> {
            employeeRepository.save(employee);
            log.info("saveEmployee(): saved employee with id: {}", employee.getId());
            return null;
        });
    }

    public void deleteEmployee(int employeeId) {
        log.info("deleteEmployee() for id: {}", employeeId);

        transactionManager.executeInTransaction(() -> {
            Employee employee = employeeRepository.findById(employeeId).orElse(null);
            if(employee == null) {
                throw new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND);
            }
            employeeRepository.delete(employee);
            log.info("deleteEmployee(): deleted employee with id: {}", employeeId);
            return null;
        });
    }

    public void requestCleaning(int roomId) {
        log.info("requestCleaning() for room: {}", roomId);

        transactionManager.executeInTransaction(() -> {
            System.out.println("Запрос на уборку комнаты " + roomId);
            employeeObserverService.notifyCleaningRequest(roomId);
            log.info("requestCleaning(): requested cleaning for room: {}", roomId);
            return null;
        });
    }

    public void requestRepair(int roomId) {
        log.info("requestRepair() for room: {}", roomId);

        transactionManager.executeInTransaction(() -> {
            System.out.println("Запрос на ремонт комнаты " + roomId);
            employeeObserverService.notifyRepairRequest(roomId);
            log.info("requestRepair(): requested repair for room: {}", roomId);
            return null;
        });
    }

    public void requestMaintenance(int roomId) {
        log.info("requestMaintenance() for room: {}", roomId);

        transactionManager.executeInTransaction(() -> {
            System.out.println("Запрос на обслуживание комнаты " + roomId);
            employeeObserverService.notifyMaintenanceRequest(roomId);
            log.info("requestMaintenance(): requested maintenance for room: {}", roomId);
            return null;
        });
    }

    public List<Employee> getAdmins() {
        log.info("getAdmins()");

        return transactionManager.executeInTransaction(() -> {
            List<Employee> admins = employeeRepository.findAll().stream()
                    .filter(e -> e.getPosition() == EmployeeRole.ADMIN)
                    .toList();
            log.info("getAdmins(): found {} admins", admins.size());
            return admins;
        });
    }

    public List<Employee> getMaids() {
        log.info("getMaids()");

        return transactionManager.executeInTransaction(() -> {
            List<Employee> maids = employeeRepository.findAll().stream()
                    .filter(e -> e.getPosition() == EmployeeRole.MAID)
                    .toList();
            log.info("getMaids(): found {} maids", maids.size());
            return maids;
        });
    }

    public List<Employee> getMenders() {
        log.info("getMenders()");

        return transactionManager.executeInTransaction(() -> {
            List<Employee> menders = employeeRepository.findAll().stream()
                    .filter(e -> e.getPosition() == EmployeeRole.MENDER)
                    .toList();
            log.info("getMenders(): found {} menders", menders.size());
            return menders;
        });
    }

    public List<Employee> findAll() {
        log.info("findAll()");

        return transactionManager.executeInTransaction(() -> {
            List<Employee> employees = employeeRepository.findAll();
            log.info("findAll(): found {} employees", employees.size());
            return employees;
        });
    }
}