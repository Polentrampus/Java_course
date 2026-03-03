package hotel;

import hotel.util.AppConfig;
import hotel.view.ConsoleMenuFactory;
import hotel.view.Menu;
import hotel.view.MenuController;
import hotel.view.Navigator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ConsoleMenuFactory consoleMenuFactory = context.getBean(ConsoleMenuFactory.class);
        runHotelApplication(consoleMenuFactory);
    }

    private static void runHotelApplication(ConsoleMenuFactory consoleMenuFactory) {
        Navigator navigator = consoleMenuFactory.getNavigator();
        if (navigator == null) {
            navigator = new Navigator();
            consoleMenuFactory.setNavigator(navigator);
        }
        Menu mainMenu = consoleMenuFactory.createMainMenu();
        navigator.setCurrentMenu(mainMenu);
        MenuController menuController = new MenuController(navigator);
        menuController.run();
    }
}