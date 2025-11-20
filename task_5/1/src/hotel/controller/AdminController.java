package hotel.controller;

import hotel.controller.manager.EmployeeManager;
import hotel.model.Hotel;
import hotel.controller.manager.ClientManager;
import hotel.controller.manager.RoomManager;
import hotel.controller.manager.ServicesManager;
import hotel.model.filter.ClientFilter;
import hotel.model.filter.RoomFilter;
import hotel.model.filter.ServicesFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.model.service.Services;
import hotel.users.client.Client;
import hotel.users.employee.Employee;
import hotel.users.employee.service.Observer;

import java.text.CollationElementIterator;
import java.time.LocalDate;
import java.util.*;

public class AdminController {
    private final Hotel hotel = Hotel.getInstance();
    private final ClientManager clientManager;
    private final ServicesManager servicesManager;
    private final RoomManager roomManager;
    private final EmployeeManager employeeManager;

    private Employee employee;
    private List<Observer> observersMaid = new ArrayList<>();
    private List<Observer> observersMender = new ArrayList<>();

    public AdminController(ClientManager clientManager, ServicesManager servicesManager,
                           List<Employee> employees, RoomManager roomManager, EmployeeManager employeeManager) {
        this.clientManager = clientManager;
        this.servicesManager = servicesManager;
        this.roomManager = roomManager;
        this.employeeManager = employeeManager;

        for (Employee employee : employees) {
            if (employee.getPosition().equals("admin")) {
                this.employee = employee;
            }
            if (employee.getPosition().equals("maid")) {
                addObserverMaid((Observer) employee);
            }
            if (employee.getPosition().equals("mender")) {
                addObserverMender((Observer) employee);
            }
        }
        addPersonal(employees);
        if (this.employee == null) {
            throw new IllegalStateException("В вашем отеле не существует " +
                    "администратора! Назначьте администратора перед использованием системы.");
        }
    }

    private void addObserverMaid(Observer observer) {
        observersMaid.add(observer);
    }

    private void addObserverMender(Observer observer) {
        observersMender.add(observer);
    }

    public void changeRoomPrice(int idRoom, int newPrice) {
        roomManager.changeRoomPrice(idRoom, newPrice);
    }

    public void addRoom(RoomCategory category, RoomStatus status, RoomType type, int roomNumber, int price, int capacity) {
        roomManager.addRoom(category, status, type, capacity, roomNumber, price);
    }

    public List<Room> getListAvailableRooms(RoomFilter filter) {
        List<Room> availableRooms = roomManager.listAvailableRooms(filter);
        System.out.println("список свободных комнат: ");
        availableRooms.stream().toList().forEach(room -> System.out.println(room.toString()));
        return availableRooms;
    }

    public List<Room> getListAvailableRoomsByDate(RoomFilter filter, LocalDate date) {
        List<Room> availableRooms = roomManager.listAvailableRoomsByDate(filter, date);
        availableRooms.stream().toList().forEach(room -> System.out.println(room.toString()));
        return availableRooms;
    }

    public void getInfoAboutRoom(int numberRoom) {
        if (hotel.getRoom(numberRoom).isEmpty()) {
            System.out.println("Нет такого номера!");
            return;
        }
        System.out.println(hotel.getRoom(numberRoom).toString());
    }

    public void requestListRoom(RoomFilter filter) {
        roomManager.requestListRoom(filter);
    }

    public void requestListRoomAndPrice(RoomFilter filter) {
        roomManager.requestListRoomAndPrice(filter);
    }

    public void addPersonal(Collection<? extends Employee> persons) {
        employeeManager.addPersonal(persons);
    }

    public Collection<Client> getInfoAboutClientDatabase(ClientFilter clientFilter) {
        clientManager.getInfoAboutClientDatabase(clientFilter);
        System.out.println("Общее число постояльцев: " + hotel.getClientMap().get().size());
        return hotel.getClientMap().get().values();
    }

    public void addClientServices(int id, List<Services> list) {
        clientManager.addService(list, id);
        System.out.println(employee.getPosition() + " обновил клиенту с ID: " + id + " услуги:\n" + clientManager.getServices(id));
    }

    public void getInfoAboutClient(int id) {
        System.out.println(clientManager.getInfoAboutClient(id));
    }

    public void requestLastThreeClient() {
        clientManager.requestLastThreeClient();
    }

    public void requestListServicesClient(ServicesFilter filter, int idClient) {
        clientManager.requestListServicesClient(filter, idClient);
    }

    public Collection<Services> requestListServices(){
        System.out.println(employee + "запросил список услуг:");
        return servicesManager.requestListServices();
    }

    public void addService(String name, String description, int price){
        servicesManager.addService(name,description,price);
    }

    public void changePriceService(String name, int price){
        servicesManager.setPrice(name,price);
    }

    public void cleaningRequest(int roomId) {
        Room room = hotel.getRoomMap().get().get(roomId);
        room.setStatus(RoomStatus.CLEANING);
        for (Observer observer : observersMaid) {
            if (room != null && room.getStatus() == RoomStatus.CLEANING) {
                observer.update(roomId);
                System.out.printf("%s изменил статус комнаты %d на %s\n",
                        employee.getPosition(), roomId, room.getStatus().getDescription());
            }
        }
    }

    public void repairRequest(int roomId) {
        for (Observer observer : observersMender) {
            Room room = hotel.getRoomMap().get().get(roomId);
            if (room != null) {
                room.setStatus(RoomStatus.MAINTENANCE);
                System.out.printf("%s изменил статус комнаты %d на %s\n",
                        employee.getPosition(), roomId, room.getStatus().getDescription());
                observer.update(roomId);
            }
        }
    }

    public void settle(Client client, int idRoom, RoomFilter roomFilter) {
        Room room = hotel.getRoomMap().get().get(idRoom);
        if (room != null && room.getNumber() != 0 && room.getStatus() == RoomStatus.AVAILABLE) {
            hotel.getClientMap().get().put(client.getId(), client);
            System.out.printf("%s поселил клиента %s\n", employee.getPosition(), hotel.getClientMap().get().get(client.getId()).toString() +
                    " в номер:" + hotel.getClientMap().get().get(client.getId()).getNumberRoom() + "\n");
            hotel.getRoomMap().get().get(idRoom).setStatus(RoomStatus.OCCUPIED);
            return;
        }
        if (room == null || room.getNumber() == 0 || room.getStatus() != RoomStatus.AVAILABLE) {
            System.out.println("Комната " + idRoom + " не существует, мы учтем ваши пожелание и подберем вам номер сами!");
            if (roomManager.listAvailableRoomsByDate(RoomFilter.ID, client.getCheckOutDate()).isEmpty()) {
                System.out.printf("Не удалось заселить клиента %s, \nнет свободных комнат к этой дате\n", client.toString());
                return;
            }
            idRoom = roomManager.listAvailableRoomsByDate(roomFilter, client.getCheckOutDate()).get(0).getNumber();
            client.setIdRoom(idRoom);
            hotel.getClientMap().get().put(client.getId(), client);
            System.out.printf("%s поселил клиента %s\n", employee.getPosition(), hotel.getClientMap().get().get(client.getId()).toString() +
                    " в номер:" + hotel.getClientMap().get().get(client.getId()).getNumberRoom() + "\n");
            hotel.getRoomMap().get().get(idRoom).setStatus(RoomStatus.OCCUPIED);
        }
    }

    public void settle(Client client, RoomFilter roomFilter) {
        if (roomManager.listAvailableRoomsByDate(RoomFilter.ID, client.getCheckOutDate()) == null) {
            System.out.printf("Не удалось заселить клиента %s, нет свободных комнат к этой дате\n", client.toString());
            return;
        }
        int idRoom = roomManager.listAvailableRoomsByDate(roomFilter, client.getCheckOutDate()).get(0).getNumber();
        client.setIdRoom(idRoom);
        hotel.getClientMap().get().put(client.getId(), client);
        System.out.printf("%s поселил клиента %s\n", employee.getPosition(), hotel.getClientMap().get().get(client.getId()).toString() +
                " в номер:" + hotel.getClientMap().get().get(client.getId()).getNumberRoom() + "\n");
        hotel.getRoomMap().get().get(idRoom).setStatus(RoomStatus.OCCUPIED);
    }

    public void settle(Client client) {
        if (roomManager.listAvailableRoomsByDate(RoomFilter.ID, client.getCheckOutDate()) == null) {
            System.out.printf("Не удалось заселить клиента %s, нет свободных комнат к этой дате\n", client.toString());
            return;
        }
        int idRoom = roomManager.listAvailableRoomsByDate(RoomFilter.ID, client.getCheckOutDate()).get(0).getNumber();
        client.setIdRoom(idRoom);
        hotel.getClientMap().get().put(client.getId(), client);
        System.out.printf("%s поселил клиента %s\n", employee.getPosition(), hotel.getClientMap().get().get(client.getId()).toString() +
                " в номер:" + hotel.getClientMap().get().get(client.getId()).getNumberRoom() + "\n");
        hotel.getRoomMap().get().get(idRoom).setStatus(RoomStatus.OCCUPIED);
    }

    public void givOutCheck(int idClient) {
        int sum = 0;
        Client client = hotel.getClientMap().get().get(idClient);
        int idRoom = client.getNumberRoom();

        sum += hotel.getRoomMap().get().get(idRoom).getPrice();
        for (Services service : client.getServicesList()) {
            sum += (int) service.getPrice();
        }
        System.out.printf("%s предоставил счет клиенту в размере: %d\n", employee.getPosition(), sum);
    }

    public void evict(int idClient) {
        Client client = hotel.getClientMap().get().get(idClient);
        if (client == null) {
            System.out.println("Такого клиента не существует!\nID: " + idClient);
            return;
        }
        int roomId = client.getNumberRoom();
        Room room = hotel.getRoomMap().get().get(roomId);

        if (roomId == 0) {
            System.out.println("У клиента нет номера для выселения");
            return;
        }
        if (room == null) {
            System.out.println("Номер " + roomId + " не существует");
            return;
        }

        givOutCheck(idClient);
        System.out.println("Клиент: " + client.getName() + " успешно выселился");
        client.setIdRoom(0);
        cleaningRequest(roomId);
    }
}
