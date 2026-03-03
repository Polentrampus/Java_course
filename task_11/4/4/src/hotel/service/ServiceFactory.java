package hotel.service;

import hotel.annotation.Component;
import hotel.annotation.Inject;
import hotel.config.HotelConfiguration;

@Component
public class ServiceFactory {
    @Inject
    private HotelConfiguration config;

    public ServiceFactory() {
    }

    public ServiceFactory(HotelConfiguration config) {
        this.config = config;
    }

    public RoomService createRoomService() {
        if (config.isRoomStatusModifiable()) {
            System.out.println("Вы можете вносить изменения!");
            return new ModifiableRoomService();
        } else
            return new ReadRoomService();
    }

    public IBookingService createBookingService() {
        IBookingService baseBookingService = new BookingService();

        if(config.isBookingHistoryEnabled()){
            System.out.println("Вы можете запросить историю бронирования!");
            return new AdvancedBookingService(config, baseBookingService);
        }
        else {
            return baseBookingService;
        }
    }
}
