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

    public static EmployeeDto from(Employee client) {
        return EmployeeDto.builder().
                id(client.getId()).
                name(client.getName()).
                surname(client.getSurname()).
                patronymic(client.getPatronymic()).
                dateOfBirth(client.getDateOfBirth()).
                build();
    }
}
