package hotel;

import hotel.controller.manager.EmployeeManager;
import hotel.model.Hotel;
import hotel.model.filter.FilterClient;
import hotel.model.filter.FilterRoom;
import hotel.model.filter.FilterServices;
import hotel.model.service.Services;
import hotel.controller.AdminController;
import hotel.controller.manager.ClientManager;
import hotel.controller.manager.RoomManager;
import hotel.controller.manager.ServicesManager;
import hotel.users.client.Client;
import hotel.users.employee.Employee;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        Employee admin = null;
        for (Employee employee: employees){
            hotel.getEmployeeMap().put(employee.getId(), employee);
            if(employee.getPosition().equals("admin")){
                admin = employee;
            }
        }

        Client client1 = new Client(1, "Анна", "Смирнова", "Игоревна",
                LocalDate.of(1990, 3, 12),
                List.of(Services.LAUNDRY),
                101, LocalDate.of(2024, 1, 15), LocalDate.of(2025, 1, 20));

        Client client2 = new Client(2, "Дмитрий", "Козлов", "Анатольевич",
                LocalDate.of(1985, 7, 25),
                Arrays.asList(Services.CONCIERGE, Services.SPA, Services.LAUNDRY, Services.BAGGAGE_STORAGE),
                205, LocalDate.of(2024, 1, 16), LocalDate.of(2025, 1, 22));

        Client client3 = new Client(3, "Светлана", "Попова", "Владимировна",
                LocalDate.of(1993, 11, 8),
                Arrays.asList(),
                312, LocalDate.of(2024, 1, 18), LocalDate.of(2025, 1, 21));

        Client client4 = new Client(4, "Михаил", "Орлов", "Сергеевич",
                LocalDate.of(1978, 5, 30),
                Arrays.asList(Services.BAGGAGE_STORAGE, Services.LAUNDRY, Services.SPA, Services.TRANSFER),
                0, LocalDate.of(2024, 1, 20), LocalDate.of(2025, 1, 27));

        Client client5 = new Client(5, "Екатерина", "Новикова", "Александровна",
                LocalDate.of(1988, 9, 14),
                Arrays.asList(Services.LAUNDRY, Services.TRANSFER, Services.BAGGAGE_STORAGE,
                        Services.SPA, Services.FITNESS_CENTER, Services.MINI_BAR),
                301, LocalDate.of(2024, 1, 22), LocalDate.of(2025, 1, 29));
        List<Client> clients = Arrays.asList(client1,client2,client3,client4,client5);

        ClientManager clientManager = new ClientManager();
        ServicesManager servicesManager = new ServicesManager(admin);
        RoomManager roomManager = new RoomManager(admin);
        EmployeeManager employeeManager = new EmployeeManager(admin);
        AdminController adminController = new AdminController(clientManager, servicesManager,
                employees, roomManager, employeeManager);

        for (Client client:clients){
            adminController.settle(client, client.getNumberRoom(), FilterRoom.ID);
        }

        adminController.requestListRoom(FilterRoom.CAPACITY);
        adminController.getListAvailableRooms(FilterRoom.PRICE);
        adminController.getInfoAboutClientDatabase(FilterClient.DATECHECKUP);
        adminController.requestLastThreeClient();
        adminController.requestListServicesClient(FilterServices.PRICE, 1);
        adminController.requestListRoomAndPrice(FilterRoom.ID);
        adminController.addClientServices(2, Arrays.asList(Services.MINI_BAR, Services.SPA));
        adminController.getInfoAboutClient(2);
        adminController.getInfoAboutRoom(100);
        adminController.evict(2);
    }
}