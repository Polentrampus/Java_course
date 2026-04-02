package hotel.config;

import hotel.repository.booking.BookingsRepository;
import hotel.repository.client.ClientRepository;
import hotel.repository.room.RoomRepository;
import hotel.service.AdvancedBookingService;
import hotel.service.BookingService;
import hotel.service.IBookingService;
import hotel.service.IRoomService;
import hotel.service.ModifiableRoomService;
import hotel.service.ReadRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:hotel.properties")
@ComponentScan({
        "hotel.service",
        "hotel.repository",
        "hotel.config",
        "hotel.dto"
})
@Import({ModuleHotelBusinessConfig.class})
public class AppConfig {
    @Value("${room.status.modifiable}")
    private boolean roomStatusModifiable;
    @Value("${bookings.deletion.allowed}")
    private boolean bookingsDeletionAllowed;
    @Value("${bookings.history.enabled}")
    private boolean bookingsHistoryEnabled;

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingsRepository bookingsRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Bean
    @DependsOn("transactionManager")
    public IRoomService roomService() {
        if (roomStatusModifiable) {
            return new ModifiableRoomService(roomRepository,
                    bookingsRepository);
        } else  {
            return new ReadRoomService(roomRepository,
                    bookingsRepository);
        }
    }

    @Bean
    @DependsOn("transactionManager")
    public IBookingService bookingService() {
        if (bookingsDeletionAllowed || bookingsHistoryEnabled) {
            return new AdvancedBookingService(bookingsRepository,
                    clientRepository,
                    roomRepository);
        } else {
            return new BookingService(bookingsRepository,
                    clientRepository,
                    roomRepository);
        }
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}