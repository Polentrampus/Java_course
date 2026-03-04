package hotel.controller;

import hotel.dto.CreateEmployeeRequest;
import hotel.dto.EmployeeDto;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/findAll")
    @Operation(
            summary = "Получить всех сотрудников",
            description = "Возвращает список всех сотрудников отеля."
    )
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        log.info("Получение списка всех сотрудников");
        List<Employee> employees = employeeService.findAll();
        return ResponseEntity.ok(employees.stream().map(EmployeeDto::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить сотрудника по ID",
            description = "Возвращает информацию о конкретном сотруднике."
    )
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable(name = "id") Integer id) throws SQLException {
        log.info("Получение сотрудника по ID: {}", id);
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if(employee.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(employee.map(EmployeeDto::from).get());

    }

    @GetMapping("position/{position}")
    @Operation(
            summary = "Получить сотрудников по должности",
            description = """
            Возвращает список сотрудников с указанной должностью.
            """
    )
    public ResponseEntity<List<EmployeeDto>> getEmployeesByPosition(
            @PathVariable(name = "position") EmployeeRole position) {
        log.info("Поиск сотрудников по должности: {}", position);
        List<Employee> employees = employeeService.getEmployeesByPosition(position);

        return ResponseEntity.ok(employees.stream().map(EmployeeDto::from).toList());
    }

    @PostMapping("/createEmployee")
    @Operation(
            summary = "Создать нового сотрудника",
            description = """
            Создание нового сотрудника отеля.
            
            ### Пример JSON:
            ```json
            {
              "name": "Иван",
              "surname": "Иванов",
              "patronymic": "Иванович",
              "dateOfBirth": "1985-05-15",
              "position": "admin",
              "salary": 50000.0
            }
            ```
            """
    )
    public ResponseEntity<EmployeeDto> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) throws SQLException {
        log.info("Создание нового сотрудника: {} {}, должность: {}",
                request.getName(), request.getSurname(), request.getPosition());
        log.debug("Данные сотрудника: {}", request);
        Optional<Employee> employee = employeeService.saveEmployee(request);
        if (employee.isEmpty()) return ResponseEntity.badRequest().build();
        log.info("Сотрудник успешно создан: ID={}, {} {}",
                employee.get().getId(), employee.get().getName(), employee.get().getSurname());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/employees/" + employee.get().getId())
                .body(employee.map(EmployeeDto::from).get());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить сотрудника",
            description = "Удаление сотрудника по ID."
    )
    public ResponseEntity<Void> deleteEmployee(@PathVariable(name = "id") Integer id) {
        log.info("Удаление сотрудника ID: {}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}