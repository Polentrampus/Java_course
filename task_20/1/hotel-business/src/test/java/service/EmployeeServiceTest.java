package service;

import config.TestHibernateConfig;
import hotel.dto.CreateEmployeeRequest;
import hotel.exception.HotelException;
import hotel.exception.employee.EmployeeInvalidRoleException;
import hotel.exception.employee.EmployeeNotFoundException;
import hotel.model.users.Person;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.repository.employee.EmployeeRepository;
import hotel.service.EmployeeObserverService;
import hotel.service.EmployeeService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestHibernateConfig.class)
@Transactional
@ActiveProfiles("test")
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeObserverService employeeObserverService;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee testEmployee;
    private CreateEmployeeRequest createEmployeeRequest;

    @BeforeEach
    public void setUp() {
        testEmployee = new Employee();
        testEmployee.setId(1);
        testEmployee.setName("Петр");
        testEmployee.setSurname("Петров");
        testEmployee.setPatronymic("Петрович");
        testEmployee.setDateOfBirth(LocalDate.of(1985, 5, 15));
        testEmployee.setPosition(EmployeeRole.ADMIN);
        testEmployee.setSalary(BigDecimal.valueOf(50000));
        testEmployee.setHireDate(LocalDate.now());

        createEmployeeRequest = new CreateEmployeeRequest();
        createEmployeeRequest.setName("Петр");
        createEmployeeRequest.setSurname("Петров");
        createEmployeeRequest.setPatronymic("Петрович");
        createEmployeeRequest.setDateOfBirth(LocalDate.of(1985, 5, 15));
        createEmployeeRequest.setPosition(EmployeeRole.ADMIN);
        createEmployeeRequest.setSalary(BigDecimal.valueOf(50000));
    }

    @Test
    void addPersonal() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(1);

        employeeService.addPersonal(List.of(testEmployee));

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void addPersonal_WithEmptyList_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> employeeService.addPersonal(Collections.emptyList()));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee() throws SQLException {
        when(employeeRepository.save(any(Employee.class))).thenReturn(1);
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));

        Optional<Employee> result = employeeService.saveEmployee(createEmployeeRequest);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void saveEmployee_WithNullName_ShouldThrowHotelException() {
        createEmployeeRequest.setName(null);

        assertThrows(HotelException.class, () -> employeeService.saveEmployee(createEmployeeRequest));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee_WithEmptyName_ShouldThrowHotelException() {
        createEmployeeRequest.setName("");

        assertThrows(HotelException.class, () -> employeeService.saveEmployee(createEmployeeRequest));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee_WithNullSurname_ShouldThrowHotelException() {
        createEmployeeRequest.setSurname(null);

        assertThrows(HotelException.class, () -> employeeService.saveEmployee(createEmployeeRequest));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee_WithEmptySurname_ShouldThrowHotelException() {
        createEmployeeRequest.setSurname("");

        assertThrows(HotelException.class, () -> employeeService.saveEmployee(createEmployeeRequest));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee_WithNullPosition_ShouldThrowEmployeeInvalidRoleException() {
        createEmployeeRequest.setPosition(null);

        assertThrows(EmployeeInvalidRoleException.class, () -> employeeService.saveEmployee(createEmployeeRequest));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee_WithNullSalary_ShouldThrowHotelException() {
        createEmployeeRequest.setSalary(null);

        assertThrows(HotelException.class, () -> employeeService.saveEmployee(createEmployeeRequest));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee_WithNegativeSalary_ShouldThrowHotelException() {
        createEmployeeRequest.setSalary(BigDecimal.valueOf(-1000));

        assertThrows(HotelException.class, () -> employeeService.saveEmployee(createEmployeeRequest));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee_WithZeroSalary_ShouldThrowHotelException() {
        createEmployeeRequest.setSalary(BigDecimal.ZERO);

        assertThrows(HotelException.class, () -> employeeService.saveEmployee(createEmployeeRequest));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee_WithFutureDateOfBirth_ShouldThrowHotelException() {
        createEmployeeRequest.setDateOfBirth(LocalDate.now().plusDays(1));

        assertThrows(HotelException.class, () -> employeeService.saveEmployee(createEmployeeRequest));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void deleteEmployee() throws SQLException {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        doNothing().when(employeeRepository).delete(any(Employee.class));

        employeeService.deleteEmployee(1);

        verify(employeeRepository).delete(testEmployee);
    }

    @Test
    void deleteEmployee_WithNullId_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> employeeService.deleteEmployee(null));
        verify(employeeRepository, never()).delete(any());
    }

    @Test
    void deleteEmployee_WithNonExistentId_ShouldThrowEmployeeNotFoundException() throws SQLException {
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(999));
        verify(employeeRepository, never()).delete(any());
    }

    @Test
    void requestCleaning() throws SQLException {
        doNothing().when(employeeObserverService).notifyCleaningRequest(101);

        employeeService.requestCleaning(101);

        verify(employeeObserverService).notifyCleaningRequest(101);
    }

    @Test
    void requestMaintenance() throws SQLException {
        doNothing().when(employeeObserverService).notifyMaintenanceRequest(101);

        employeeService.requestMaintenance(101);

        verify(employeeObserverService).notifyMaintenanceRequest(101);
    }

    @Test
    void getAdmins() {
        when(employeeRepository.findAll()).thenReturn(List.of(testEmployee));

        List<Employee> admins = employeeService.getAdmins();

        assertThat(admins).hasSize(1);
        verify(employeeRepository).findAll();
    }

    @Test
    void getAdmins_ShouldReturnEmptyList() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        List<Employee> admins = employeeService.getAdmins();

        assertThat(admins).isEmpty();
        verify(employeeRepository).findAll();
    }

    @Test
    void getMaids() {
        Employee maid = new Employee();
        maid.setPosition(EmployeeRole.MAID);
        when(employeeRepository.findAll()).thenReturn(List.of(maid));

        List<Employee> maids = employeeService.getMaids();

        assertThat(maids).hasSize(1);
        verify(employeeRepository).findAll();
    }

    @Test
    void getMaids_ShouldReturnEmptyList() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        List<Employee> maids = employeeService.getMaids();

        assertThat(maids).isEmpty();
        verify(employeeRepository).findAll();
    }

    @Test
    void getMenders() {
        Employee mender = new Employee();
        mender.setPosition(EmployeeRole.MENDER);
        when(employeeRepository.findAll()).thenReturn(List.of(mender));

        List<Employee> menders = employeeService.getMenders();

        assertThat(menders).hasSize(1);
        verify(employeeRepository).findAll();
    }

    @Test
    void getMenders_ShouldReturnEmptyList() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        List<Employee> menders = employeeService.getMenders();

        assertThat(menders).isEmpty();
        verify(employeeRepository).findAll();
    }

    @Test
    void findAll() {
        when(employeeRepository.findAll()).thenReturn(Collections.singletonList(testEmployee));

        List<Employee> employees = employeeService.findAll();

        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getId()).isEqualTo(1);
        verify(employeeRepository).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        List<Employee> employees = employeeService.findAll();

        assertThat(employees).isEmpty();
        verify(employeeRepository).findAll();
    }

    @Test
    void getEmployeeById() throws SQLException {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));

        Optional<Employee> result = employeeService.getEmployeeById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        verify(employeeRepository).findById(1);
    }

    @Test
    void getEmployeeById_ShouldReturnEmpty() throws SQLException {
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Employee> result = employeeService.getEmployeeById(999);

        assertThat(result).isEmpty();
        verify(employeeRepository).findById(999);
    }

    @Test
    void getEmployeeById_WithNullId_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> employeeService.getEmployeeById(null));
        verify(employeeRepository, never()).findById(any());
    }

    @Test
    void getEmployeesByPosition() {
        when(employeeRepository.findByRole(EmployeeRole.ADMIN)).thenReturn(Collections.singletonList(testEmployee));

        List<Employee> employees = employeeService.getEmployeesByPosition(EmployeeRole.ADMIN);

        assertThat(employees).hasSize(1);
        verify(employeeRepository).findByRole(EmployeeRole.ADMIN);
    }

    @Test
    void getEmployeesByPosition_ShouldReturnEmptyList() {
        when(employeeRepository.findByRole(EmployeeRole.MAID)).thenReturn(Collections.emptyList());

        List<Employee> employees = employeeService.getEmployeesByPosition(EmployeeRole.MAID);

        assertThat(employees).isEmpty();
        verify(employeeRepository).findByRole(EmployeeRole.MAID);
    }

    @Test
    void getEmployeesByPosition_WithNullPosition_ShouldThrowEmployeeInvalidRoleException() {
        assertThrows(EmployeeInvalidRoleException.class, () -> employeeService.getEmployeesByPosition(null));
        verify(employeeRepository, never()).findByRole(any());
    }
}