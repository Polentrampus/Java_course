package hotel.dto;

import hotel.model.security.Role;
import hotel.model.security.User;
import hotel.model.users.Person;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private Set<Role> roles;
    private PersonDto personDto;

    public UserDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.id = user.getId();
        this.personDto = new PersonDto(user.getPerson());
        this.roles = user.getRoles();
    }
}
