package hotel.service;

import hotel.dto.CreateEmployeeRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.dao.DAOException;
import hotel.exception.employee.EmployeeInvalidRoleException;
import hotel.exception.employee.EmployeeNotFoundException;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.repository.employee.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeObserverService employeeObserverService;
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           EmployeeObserverService employeeObserverService) {
        this.employeeRepository = employeeRepository;
        this.employeeObserverService = employeeObserverService;
    }

    public void addPersonal(Collection<? extends Employee> persons) {
        log.info("Adding {} employees", persons.size());

        if (persons == null || persons.isEmpty()) {
            log.warn("No employees to add");
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Список сотрудников пуст");
        }

        try {
            for (Employee person : persons) {
                validateEmployee(person);
                employeeRepository.save(person);
                log.info("Added employee: {} {}", person.getName(), person.getSurname());
            }
            log.info("Successfully added {} employees", persons.size());

        } catch (DAOException e) {
            log.error("Database error while adding employees", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при добавлении сотрудников", e);
        } catch (Exception e) {
            log.error("Unexpected error while adding employees", e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при добавлении сотрудников", e);
        }
    }

    public Optional<Employee> saveEmployee(CreateEmployeeRequest request) {
        log.info("Saving new employee: {} {}", request.getName(), request.getSurname());

        validateCreateEmployeeRequest(request);

        try {
            Employee employee = new Employee();
            employee.setName(request.getName());
            employee.setSurname(request.getSurname());
            employee.setPatronymic(request.getPatronymic());
            employee.setDateOfBirth(request.getDateOfBirth());
            employee.setSalary(request.getSalary());
            employee.setPosition(request.getPosition());
            employee.setHireDate(LocalDate.now());

            Integer employeeId = employeeRepository.save(employee);
            log.info("Employee saved with id: {}", employeeId);

            return Optional.ofNullable(employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new HotelException(ErrorCode.DATA_INTEGRITY_VIOLATION,
                            "Не удалось найти сохраненного сотрудника")));

        } catch (DAOException e) {
            log.error("Database error while saving employee", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при сохранении сотрудника", e);
        } catch (Exception e) {
            log.error("Unexpected error while saving employee", e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при сохранении сотрудника", e);
        }
    }

    public void deleteEmployee(Integer employeeId) {
        log.info("Deleting employee with id: {}", employeeId);

        if (employeeId == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID сотрудника не может быть null");
        }

        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

            employeeRepository.delete(employee);
            log.info("Employee deleted successfully with id: {}", employeeId);

        } catch (EmployeeNotFoundException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while deleting employee with id: {}", employeeId, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при удалении сотрудника", e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting employee with id: {}", employeeId, e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при удалении сотрудника", e);
        }
    }

    public void requestCleaning(int roomId) {
        log.info("Requesting cleaning for room: {}", roomId);

        try {
            System.out.println("Запрос на уборку комнаты " + roomId);
            employeeObserverService.notifyCleaningRequest(roomId);
            log.info("Cleaning request sent for room: {}", roomId);

        } catch (Exception e) {
            log.error("Error while requesting cleaning for room: {}", roomId, e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Ошибка при отправке запроса на уборку", e);
        }
    }

    public void requestMaintenance(int roomId) {
        log.info("Requesting maintenance for room: {}", roomId);

        try {
            System.out.println("Запрос на обслуживание комнаты " + roomId);
            employeeObserverService.notifyMaintenanceRequest(roomId);
            log.info("Maintenance request sent for room: {}", roomId);

        } catch (Exception e) {
            log.error("Error while requesting maintenance for room: {}", roomId, e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Ошибка при отправке запроса на обслуживание", e);
        }
    }

    public List<Employee> getAdmins() {
        log.info("Getting all admins");

        try {
            List<Employee> admins = employeeRepository.findAll().stream()
                    .filter(e -> e.getPosition() == EmployeeRole.ADMIN)
                    .toList();
            log.info("Found {} admins", admins.size());
            return admins;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while getting admins", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при получении списка администраторов", e);
        }
    }

    public List<Employee> getMaids() {
        log.info("Getting all maids");

        try {
            List<Employee> maids = employeeRepository.findAll().stream()
                    .filter(e -> e.getPosition() == EmployeeRole.MAID)
                    .toList();
            log.info("Found {} maids", maids.size());
            return maids;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while getting maids", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при получении списка горничных", e);
        }
    }

    public List<Employee> getMenders() {
        log.info("Getting all menders");

        try {
            List<Employee> menders = employeeRepository.findAll().stream()
                    .filter(e -> e.getPosition() == EmployeeRole.MENDER)
                    .toList();
            log.info("Found {} menders", menders.size());
            return menders;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while getting menders", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при получении списка мастеров", e);
        }
    }

    public List<Employee> findAll() {
        log.info("Finding all employees");

        try {
            List<Employee> employees = employeeRepository.findAll();
            log.info("Found {} employees", employees.size());
            return employees;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while finding all employees", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при получении списка всех сотрудников", e);
        }
    }

    public Optional<Employee> getEmployeeById(Integer id) {
        log.info("Getting employee by id: {}", id);

        if (id == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID сотрудника не может быть null");
        }

        try {
            Optional<Employee> employee = employeeRepository.findById(id);
            log.info("Employee found: {}", employee.isPresent());
            return employee;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while getting employee by id: {}", id, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при получении сотрудника по ID", e);
        }
    }

    public List<Employee> getEmployeesByPosition(EmployeeRole position) {
        log.info("Getting employees by position: {}", position);

        if (position == null) {
            throw new EmployeeInvalidRoleException("null");
        }

        try {
            List<Employee> employees = employeeRepository.findByRole(position);
            log.info("Found {} employees with position {}", employees.size(), position);
            return employees;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while getting employees by position: {}", position, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при получении сотрудников по должности", e);
        }
    }

    private void validateEmployee(Employee employee) {
        if (employee == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Сотрудник не может быть null");
        }
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Имя сотрудника обязательно");
        }
        if (employee.getSurname() == null || employee.getSurname().trim().isEmpty()) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Фамилия сотрудника обязательна");
        }
        if (employee.getPosition() == null) {
            throw new EmployeeInvalidRoleException("null");
        }
        if (employee.getSalary() == null || employee.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR,
                    "Зарплата должна быть положительным числом");
        }
    }

    private void validateCreateEmployeeRequest(CreateEmployeeRequest request) {
        if (request == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Запрос на создание сотрудника не может быть null");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Имя сотрудника обязательно");
        }
        if (request.getSurname() == null || request.getSurname().trim().isEmpty()) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Фамилия сотрудника обязательна");
        }
        if (request.getPosition() == null) {
            throw new EmployeeInvalidRoleException("null");
        }
        if (request.getSalary() == null || request.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR,
                    "Зарплата должна быть положительным числом");
        }
        if (request.getDateOfBirth() != null && request.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR,
                    "Дата рождения не может быть в будущем");
        }
    }
}