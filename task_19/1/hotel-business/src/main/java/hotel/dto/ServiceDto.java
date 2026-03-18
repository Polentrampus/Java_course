package hotel.dto;

import hotel.model.service.Services;
import hotel.model.users.employee.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDto {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;

    public static ServiceDto from(Services services) {
        return ServiceDto.builder().
                id(services.getId()).
                name(services.getName()).
                description(services.getDescription()).
                price(services.getPrice()).
                build();
    }
}