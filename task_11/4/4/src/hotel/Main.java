package hotel;

import hotel.config.PropertiesConfiguration;
import hotel.di.DIContainer;
import hotel.service.EmployeeService;
import hotel.view.ConsoleMenuFactory;
import hotel.view.Menu;
import hotel.view.MenuController;
import hotel.view.Navigator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        PropertiesConfiguration config = new PropertiesConfiguration();
        DIContainer container = DIContainer.getInstance();
        container.init("hotel");

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