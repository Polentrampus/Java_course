package hotel;

import hotel.controller.manager.EmployeeManager;
import hotel.model.Hotel;
import hotel.model.filter.RoomFilter;
import hotel.controller.AdminController;
import hotel.controller.manager.ClientManager;
import hotel.controller.manager.RoomManager;
import hotel.controller.manager.ServicesManager;
import hotel.users.client.Client;
import hotel.users.employee.Admin;
import hotel.users.employee.Employee;
import hotel.view.ConsoleMenuFactory;
import hotel.view.Menu;
import hotel.view.MenuController;
import hotel.view.Navigator;

import java.time.LocalDate;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Hotel hotel = Hotel.getInstance();
        List<Employee> employees = new ArrayList<>();

        employees.add(Employee.createEmployee(1, "Иван", "Петров", "Сергеевич",
                LocalDate.of(1985, 5, 15), "admin"));
        employees.add(Employee.createEmployee(2, "Мария", "Иванова", "Александровна",
                LocalDate.of(1990, 8, 22), "maid"));
        employees.add(Employee.createEmployee(3, "Алексей", "Сидоров", "Викторович",
                LocalDate.of(1988, 3, 10), "mender"));
        employees.add(Employee.createEmployee(4, "Елена", "Кузнецова", "Дмитриевна",
                LocalDate.of(1992, 11, 5), "admin"));
        employees.add(Employee.createEmployee(5, "Ольга", "Васильева", "Николаевна",
                LocalDate.of(1995, 7, 30), "maid"));


        Client client1 = new Client(1, "Анна", "Смирнова", "Игоревна",
                LocalDate.of(1990, 3, 12),
                hotel.getServicesByKeys("MINI_BAR"),
                101, LocalDate.of(2024, 1, 15), LocalDate.of(2025, 1, 20));

        Client client2 = new Client(2, "Дмитрий", "Козлов", "Анатольевич",
                LocalDate.of(1985, 7, 25),
                hotel.getServicesByKeys("BAGGAGE_STORAGE", "SPA", "MINI_BAR"),
                205, LocalDate.of(2024, 1, 16), LocalDate.of(2025, 1, 22));

        Client client3 = new Client(3, "Светлана", "Попова", "Владимировна",
                LocalDate.of(1993, 11, 8),
                hotel.getServicesByKeys(),
                312, LocalDate.of(2024, 1, 18), LocalDate.of(2025, 1, 21));

        Client client4 = new Client(4, "Михаил", "Орлов", "Сергеевич",
                LocalDate.of(1978, 5, 30),
                hotel.getServicesByKeys("SPA", "BAGGAGE_STORAGE", "TRANSFER"),
                0, LocalDate.of(2024, 1, 20), LocalDate.of(2025, 1, 27));

        Client client5 = new Client(5, "Екатерина", "Новикова", "Александровна",
                LocalDate.of(1988, 9, 14),
                hotel.getServicesByKeys("MINI_BAR", "BAGGAGE_STORAGE"),
                301, LocalDate.of(2024, 1, 22), LocalDate.of(2025, 1, 29));
        List<Client> clients = Arrays.asList(client1, client2, client3, client4, client5);

        Admin admin = null;
        Optional<Admin> adminCur = employees.stream().filter(Objects::nonNull).
                filter(e -> e instanceof Admin).map(e -> (Admin) e).findFirst();

        if (adminCur.isPresent()) {
            admin = adminCur.get();
            System.out.println("Вы назначили админом: " + admin.toString());
        } else {
            admin = new Admin(1, "name", "surname", "patronymic",
                    LocalDate.now());
            employees.add(admin);
            System.out.println("Вы не назначили админа, поэтому зашли в систему под именем супер пользователя: ");
        }

        ClientManager clientManager = new ClientManager(admin);
        ServicesManager servicesManager = new ServicesManager(admin);
        RoomManager roomManager = new RoomManager(admin);
        EmployeeManager employeeManager = new EmployeeManager(admin);
        AdminController adminController = new AdminController(clientManager, servicesManager,
                employees, roomManager, employeeManager);

        for (Client client : clients) {
            adminController.settle(client, client.getNumberRoom(), RoomFilter.ID);
        }

        Navigator navigator = new Navigator();
        ConsoleMenuFactory consoleMenuFactory = new ConsoleMenuFactory(adminController, navigator);
        Menu mainMenu = consoleMenuFactory.createMainMenu();
        navigator.setCurrentMenu(mainMenu);
        MenuController menuController = new MenuController(navigator);
        menuController.run();
    }
}