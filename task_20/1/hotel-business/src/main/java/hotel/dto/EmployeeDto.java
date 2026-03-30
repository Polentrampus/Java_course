package hotel.dto;

import hotel.model.users.client.Client;
import hotel.model.users.employee.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDto {
    private Integer id;
    private String name;
    private String surname;
    private String patronymic;
    private LocalDate dateOfBirth;
    private String position;

    public static EmployeeDto from(Employee employee) {
        return EmployeeDto.builder().
                id(employee.getId()).
                name(employee.getName()).
                surname(employee.getSurname()).
                patronymic(employee.getPatronymic()).
                dateOfBirth(employee.getDateOfBirth()).
                position(String.valueOf(employee.getPosition())).
                build();
    }
}
