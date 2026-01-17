package hotel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hotel.config.HotelConfiguration;
import hotel.config.PropertiesConfiguration;
import hotel.di.DIContainer;
import hotel.dto.HotelDto;
import hotel.dto.HotelDtoDeserializer;
import hotel.exception.JsonDeserializerException;
import hotel.exception.booking.BookingException;
import hotel.exception.client.ClientException;
import hotel.exception.employee.EmployeeException;
import hotel.exception.room.RoomException;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
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
//            loadHotel();
            adminController = initializeHotelWithSampleData(container);
        } catch (Exception e) {
            System.out.println("Не удалось загрузить, создаем новый отель");
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


    static void loadHotel() throws IOException, JsonDeserializerException {
        File file = new File("hotel.json");
        if (!file.exists()) {
            throw new IOException("Файл hotel.json не найден");
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        System.out.println("Загрузка отеля из файла: " + file.getAbsolutePath());

        try {
            HotelDto data = mapper.readValue(file, HotelDto.class);
            Hotel hotel = Hotel.getInstance();

            if (data.getRooms() != null && !data.getRooms().isEmpty()) {
                System.out.println("Загружено комнат: " + data.getRooms().size());
            } else {
                throw new JsonDeserializerException(Room.class, "Нет данных о комнатах");
            }

            if (data.getEmployees() != null && !data.getEmployees().isEmpty()) {
                System.out.println("Загружено сотрудников: " + data.getEmployees().size());
            } else {
                throw new JsonDeserializerException(Employee.class, "Нет данных о сотрудниках");
            }
            if (data.getClients() != null && !data.getClients().isEmpty()) {
                System.out.println("Загружено клиентов: " + data.getClients().size());
                if (hotel.getClientMap().isPresent()) {
                    hotel.getClientMap().get().putAll(data.getClients());
                } else {
                    throw new IllegalStateException("ClientMap не инициализирован в Hotel");
                }
            } else {
                throw new JsonDeserializerException(Client.class, "Нет данных о клиентах");
            }

            // Загружаем бронирования (может быть null или пустым)
            if (data.getBookings() != null) {
                System.out.println("Загружено бронирований: " + data.getBookings().size());
                if (hotel.getBookingsMap().isPresent()) {
                    hotel.getBookingsMap().get().putAll(data.getBookings());
                } else {
                    throw new IllegalStateException("BookingsMap не инициализирован в Hotel");
                }
            } else {
                System.out.println("Бронирования отсутствуют в файле");
            }

            System.out.println("Отель успешно загружен из файла!");

        } catch (JsonProcessingException e) {
            System.err.println("Ошибка парсинга JSON: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Неверный формат JSON файла", e);
        }
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
                LocalDate.of(1990, 3, 12));

        Client client2 = new Client(2, "Дмитрий", "Козлов", "Анатольевич",
                LocalDate.of(1985, 7, 25));

        Client client3 = new Client(3, "Светлана", "Попова", "Владимировна",
                LocalDate.of(1993, 11, 8));

        Client client4 = new Client(4, "Михаил", "Орлов", "Сергеевич",
                LocalDate.of(1978, 5, 30));

        Client client5 = new Client(5, "Екатерина", "Новикова", "Александровна",
                LocalDate.of(1988, 9, 14));

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

        adminController.settle(client1, LocalDate.of(2026, 4, 17), 2);
        adminController.settle(client2, LocalDate.of(2026, 3, 17), 7);
        adminController.settle(client3, LocalDate.of(2026, 1, 27), 1);
        adminController.settle(client4, LocalDate.of(2026, 1, 17), 13);
        adminController.settle(client5);

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