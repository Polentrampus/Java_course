package hotel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hotel.model.users.employee.EmployeeRole;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CreateEmployeeRequest {

    @NotBlank(message = "Имя обязательно")
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    private String surname;

    private String patronymic;

    @NotNull(message = "Дата рождения обязательна")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate dateOfBirth;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "Должность обязательна")
    private EmployeeRole position;

    @NotNull(message = "Зарплата обязательна")
    @Min(value = 0, message = "Зарплата не может быть отрицательной")
    private BigDecimal salary;
}
