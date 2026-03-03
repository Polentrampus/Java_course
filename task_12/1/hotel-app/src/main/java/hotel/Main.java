package hotel;

import hotel.config.PropertiesConfiguration;
import hotel.di.DIContainer;
import hotel.model.room.Room;
import hotel.repository.room.JdbcRoomRepository;
import hotel.service.AdvancedBookingService;
import hotel.service.BookingService;
import hotel.service.ClientService;
import hotel.service.EmployeeObserverService;
import hotel.service.EmployeeService;
import hotel.service.ModifiableRoomService;
import hotel.service.ReadRoomService;
import hotel.service.ServicesService;
import hotel.view.ConsoleMenuFactory;
import hotel.view.Menu;
import hotel.view.MenuController;
import hotel.view.Navigator;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        PropertiesConfiguration config = new PropertiesConfiguration();
        DIContainer container = DIContainer.getInstance();

        container.init(List.of(
                PropertiesConfiguration.class,
                ReadRoomService.class,
                ModifiableRoomService.class,
                ClientService.class,
                EmployeeObserverService.class,
                EmployeeService.class,
                ServicesService.class,
                AdvancedBookingService.class,
                BookingService.class,
                JdbcRoomRepository.class,
                ConsoleMenuFactory.class,
                Navigator.class,
                Room.class), config);

        ConsoleMenuFactory consoleMenuFactory = container.getBean(ConsoleMenuFactory.class);
        if (consoleMenuFactory == null) {
            throw new RuntimeException("ConsoleMenuFactory не найден в контейнере");
        }
        runHotelApplication(consoleMenuFactory);
    }

    private static void runHotelApplication(ConsoleMenuFactory consoleMenuFactory) throws IOException {
        Navigator navigator = consoleMenuFactory.getNavigator();
        if (navigator == null) {
            navigator = new Navigator();
            consoleMenuFactory.setNavigator(navigator);
        }
        Menu mainMenu = consoleMenuFactory.downloadMenu();
        navigator.setCurrentMenu(mainMenu);
        MenuController menuController = new MenuController(navigator);
        menuController.run();
    }
}