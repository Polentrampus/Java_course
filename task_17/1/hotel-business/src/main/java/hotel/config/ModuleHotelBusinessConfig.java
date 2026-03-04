package hotel.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "hotel.model", "hotel.exception", "hotel.database"
})
public class ModuleHotelBusinessConfig {
}
