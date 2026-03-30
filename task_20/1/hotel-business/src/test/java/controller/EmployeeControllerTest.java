package controller;

import config.TestHibernateConfig;
import hotel.controller.EmployeeController;
import hotel.dto.CreateEmployeeRequest;
import hotel.dto.EmployeeDto;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.service.EmployeeService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ContextConfiguration(classes = TestHibernateConfig.class)
@ActiveProfiles("test")
public class EmployeeControllerTest extends BaseController {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private Employee testEmployee;
    private CreateEmployeeRequest createEmployeeRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();
        setUpMockMvc(employeeController);

        testEmployee = new Employee();
        testEmployee.setId(1);
        testEmployee.setName("Petr");
        testEmployee.setSurname("Petrov");
        testEmployee.setPatronymic("Petrovich");
        testEmployee.setDateOfBirth(LocalDate.of(1985, 5, 15));
        testEmployee.setPosition(EmployeeRole.ADMIN);
        testEmployee.setSalary(BigDecimal.valueOf(50000));

        createEmployeeRequest = new CreateEmployeeRequest();
        createEmployeeRequest.setName("Petr");
        createEmployeeRequest.setSurname("Petrov");
        createEmployeeRequest.setPatronymic("Petrovich");
        createEmployeeRequest.setDateOfBirth(LocalDate.of(1985, 5, 15));
        createEmployeeRequest.setPosition(EmployeeRole.ADMIN);
        createEmployeeRequest.setSalary(BigDecimal.valueOf(50000));
    }

    @Test
    void getAllEmployees() throws Exception {
        when(employeeService.findAll()).thenReturn(Collections.singletonList(testEmployee));

        MvcResult mvcResult = mockMvc.perform(get("/employees/findAll"))
                .andExpect(status().isOk())
                .andReturn();

        EmployeeDto employeeDto = EmployeeDto.from(testEmployee);
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(asJsonString(List.of(employeeDto)));
    }

    @Test
    void getEmployeeById() throws Exception {
        when(employeeService.getEmployeeById(1)).thenReturn(Optional.of(testEmployee));

        MvcResult mvcResult = mockMvc.perform(get("/employees/getById/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        EmployeeDto employeeDto = EmployeeDto.from(testEmployee);
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(asJsonString(employeeDto));
    }

    @Test
    void getEmployeeById_NotFound() throws Exception {
        when(employeeService.getEmployeeById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/employees/getById/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEmployeesByPosition() throws Exception {
        when(employeeService.getEmployeesByPosition(EmployeeRole.ADMIN))
                .thenReturn(Collections.singletonList(testEmployee));

        MvcResult mvcResult = mockMvc.perform(get("/employees/position/{position}", "ADMIN"))
                .andExpect(status().isOk())
                .andReturn();

        EmployeeDto employeeDto = EmployeeDto.from(testEmployee);
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(asJsonString(List.of(employeeDto)));
    }

    @Test
    void createEmployee() throws Exception {
        when(employeeService.saveEmployee(any(CreateEmployeeRequest.class)))
                .thenReturn(Optional.of(testEmployee));

        mockMvc.perform(post("/employees/createEmployee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createEmployeeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Petr"))
                .andExpect(jsonPath("$.position").value(EmployeeRole.ADMIN.toString()));

        verify(employeeService).saveEmployee(any(CreateEmployeeRequest.class));
    }

    @Test
    void createEmployee_BadRequest() throws Exception {
        when(employeeService.saveEmployee(any(CreateEmployeeRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/employees/createEmployee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createEmployeeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteEmployee() throws Exception {
        mockMvc.perform(delete("/employees/delete/{id}", 1))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee(1);
    }

    @Test
    void createEmployee_WithInvalidDateOfBirth_ShouldReturnBadRequest() throws Exception {
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest();
        invalidRequest.setName("Petr");
        invalidRequest.setSurname("Petrov");
        invalidRequest.setPatronymic("Petrovich");
        invalidRequest.setDateOfBirth(LocalDate.now().plusDays(1)); // дата в будущем
        invalidRequest.setPosition(EmployeeRole.ADMIN);
        invalidRequest.setSalary(BigDecimal.valueOf(50000));

        mockMvc.perform(post("/employees/createEmployee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmployee_WithNullName_ShouldReturnBadRequest() throws Exception {
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest();
        invalidRequest.setName(null);
        invalidRequest.setSurname("Petrov");
        invalidRequest.setPatronymic("Petrovich");
        invalidRequest.setDateOfBirth(LocalDate.of(1985, 5, 15));
        invalidRequest.setPosition(EmployeeRole.ADMIN);
        invalidRequest.setSalary(BigDecimal.valueOf(50000));

        mockMvc.perform(post("/employees/createEmployee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmployee_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest();
        invalidRequest.setName("");
        invalidRequest.setSurname("Petrov");
        invalidRequest.setPatronymic("Petrovich");
        invalidRequest.setDateOfBirth(LocalDate.of(1985, 5, 15));
        invalidRequest.setPosition(EmployeeRole.ADMIN);
        invalidRequest.setSalary(BigDecimal.valueOf(50000));

        mockMvc.perform(post("/employees/createEmployee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmployee_WithNullSurname_ShouldReturnBadRequest() throws Exception {
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest();
        invalidRequest.setName("Petr");
        invalidRequest.setSurname(null);
        invalidRequest.setPatronymic("Petrovich");
        invalidRequest.setDateOfBirth(LocalDate.of(1985, 5, 15));
        invalidRequest.setPosition(EmployeeRole.ADMIN);
        invalidRequest.setSalary(BigDecimal.valueOf(50000));

        mockMvc.perform(post("/employees/createEmployee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmployee_WithEmptySurname_ShouldReturnBadRequest() throws Exception {
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest();
        invalidRequest.setName("Petr");
        invalidRequest.setSurname("");
        invalidRequest.setPatronymic("Petrovich");
        invalidRequest.setDateOfBirth(LocalDate.of(1985, 5, 15));
        invalidRequest.setPosition(EmployeeRole.ADMIN);
        invalidRequest.setSalary(BigDecimal.valueOf(50000));

        mockMvc.perform(post("/employees/createEmployee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmployee_WithNullPosition_ShouldReturnBadRequest() throws Exception {
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest();
        invalidRequest.setName("Petr");
        invalidRequest.setSurname("Petrov");
        invalidRequest.setPatronymic("Petrovich");
        invalidRequest.setDateOfBirth(LocalDate.of(1985, 5, 15));
        invalidRequest.setPosition(null);
        invalidRequest.setSalary(BigDecimal.valueOf(50000));

        mockMvc.perform(post("/employees/createEmployee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmployee_WithNegativeSalary_ShouldReturnBadRequest() throws Exception {
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest();
        invalidRequest.setName("Petr");
        invalidRequest.setSurname("Petrov");
        invalidRequest.setPatronymic("Petrovich");
        invalidRequest.setDateOfBirth(LocalDate.of(1985, 5, 15));
        invalidRequest.setPosition(EmployeeRole.ADMIN);
        invalidRequest.setSalary(BigDecimal.valueOf(-1000));

        mockMvc.perform(post("/employees/createEmployee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmployee_WithNullSalary_ShouldReturnBadRequest() throws Exception {
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest();
        invalidRequest.setName("Petr");
        invalidRequest.setSurname("Petrov");
        invalidRequest.setPatronymic("Petrovich");
        invalidRequest.setDateOfBirth(LocalDate.of(1985, 5, 15));
        invalidRequest.setPosition(EmployeeRole.ADMIN);
        invalidRequest.setSalary(null);

        mockMvc.perform(post("/employees/createEmployee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

}