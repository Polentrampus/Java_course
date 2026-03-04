package hotel.dto;

import hotel.model.users.client.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private Integer id;
    private String name;
    private String surname;
    private String patronymic;
    private LocalDate dateOfBirth;

    public static ClientDto from(Client client) {
        return ClientDto.builder().
                id(client.getId()).
                name(client.getName()).
                surname(client.getSurname()).
                patronymic(client.getPatronymic()).
                dateOfBirth(client.getDateOfBirth()).
                build();
    }
}