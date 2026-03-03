package hotel.config;

import hotel.ModuleHotelCoreConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ModuleHotelCoreConfig.class})
@ComponentScan(basePackages = {
    "hotel.config", "hotel.dto", "hotel.repository", "hotel.service", "hotel.util"
})
public class ModuleHotelBusinessConfig {
}
