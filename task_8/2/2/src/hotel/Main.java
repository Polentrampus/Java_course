package hotel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hotel.config.HotelConfiguration;
import hotel.config.PropertiesConfiguration;
import hotel.di.DIContainer;
import hotel.dto.HotelDto;
import hotel.service.*;
import hotel.model.Hotel;
import hotel.controller.AdminController;
import hotel.model.users.client.Client;
import hotel.model.users.employee.Admin;
import hotel.model.users.employee.Employee;
import hotel.view.ConsoleMenuFactory;
import hotel.view.Menu;
import hotel.view.MenuController;
import hotel.view.Navigator;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class Main {
    private static final ObjectMapper mapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // красивое форматирование
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(
                com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
        );
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return mapper;
    }

    public static void main(String[] args) throws Exception {
        DIContainer container = new DIContainer();
        container.init("hotel");

        AdminController adminController = container.getBean(AdminController.class);
        try {
            loadHotel();
        } catch (Exception e) {
            System.out.println("Не удалось загрузить, создаем новый отель");
            adminController = initializeHotelWithSampleData(container);
        }
        runHotelApplication(adminController);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveHotel();
                System.out.println("Данные сохранены");
            } catch (IOException e) {
                System.err.println("Ошибка сохранения: " + e.getMessage());
            }
        }));
    }

    static void saveHotel() throws IOException {
        Hotel hotel = Hotel.getInstance();

        HotelDto data = new HotelDto(
                hotel.getRoomMap().get(),
                hotel.getEmployeeMap().get(),
                hotel.getClientMap().get(),
                hotel.getBookingsMap().get()
        );

        mapper.writeValue(new File("hotel.json"), data);
        System.out.println("Отель сохранен");
    }

    static void loadHotel() throws IOException {
        File file = new File("hotel.json");
        if (!file.exists()) {
            throw new IOException("Файл не найден");
        }

        HotelDto data = mapper.readValue(file, HotelDto.class);
        Hotel hotel = Hotel.getInstance();

        // Восстанавливаем данные
        if (data.getRooms() != null) hotel.getRoomMap().get().putAll(data.getRooms());
        if (data.getEmployees() != null) hotel.getEmployeeMap().get().putAll(data.getEmployees());
        if (data.getClients() != null) hotel.getClientMap().get().putAll(data.getClients());
        if (data.getBookings() != null) hotel.getBookingsMap().get().putAll(data.getBookings());

        System.out.println("Отель загружен: " +
                data.getRooms().size() + " комнат, " +
                data.getClients().size() + " клиентов");
    }

    private static AdminController initializeHotelWithSampleData(DIContainer container) throws Exception {
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

        HotelConfiguration config = container.getBean(PropertiesConfiguration.class);
        ServiceFactory serviceFactory = container.getBean(ServiceFactory.class);
        ClientService clientService = container.getBean(ClientService.class);
        ServicesService servicesService = container.getBean(ServicesService.class);
        EmployeeService employeeService = container.getBean(EmployeeService.class);
        BookingService bookingService = container.getBean(BookingService.class);
        AdminController adminController = container.getBean(AdminController.class);
        
        adminController.initialize(employees, config, serviceFactory);

        for (Client client : clients) {
            adminController.settle(client, client.getCheckOutDate());
        }

        System.out.println("Тестовые данные созданы");
        return adminController;
    }

    private static void runHotelApplication(AdminController adminController) throws IOException {
        Navigator navigator = new Navigator();
        ConsoleMenuFactory consoleMenuFactory = new ConsoleMenuFactory(adminController, navigator);
        Menu mainMenu = consoleMenuFactory.createMainMenu();
        navigator.setCurrentMenu(mainMenu);
        MenuController menuController = new MenuController(navigator);
        menuController.run();
    }

}