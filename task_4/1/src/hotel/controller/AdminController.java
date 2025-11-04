package hotel.controller;

import hotel.controller.manager.EmployeeManager;
import hotel.model.Hotel;
import hotel.controller.manager.ClientManager;
import hotel.controller.manager.RoomManager;
import hotel.controller.manager.ServicesManager;
import hotel.model.filter.FilterClient;
import hotel.model.filter.FilterRoom;
import hotel.model.filter.FilterServices;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.model.service.Services;
import hotel.users.client.Client;
import hotel.users.employee.Employee;
import hotel.users.employee.service.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class AdminController  {
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

        for(Employee employee:employees){
            if(employee.getPosition().equals("admin")){
                this.employee = employee;
            }
            if(employee.getPosition().equals("maid")){
                addObserverMaid((Observer) employee);
            }
            if(employee.getPosition().equals("mender")){
                addObserverMender((Observer) employee);
            }
        }
        addPersonal(employees);
        if (this.employee == null){
            throw new IllegalStateException("В вашем отеле не существует " +
                    "администратора! Назначьте администратора перед использованием системы.");
        }
    }

    public void addObserverMaid(Observer observer) {
        observersMaid.add(observer);
    }

    public void addObserverMender(Observer observer) {
        observersMender.add(observer);
    }

    public void changeRoomPrice(int idRoom, int newPrice){
        roomManager.changeRoomPrice(idRoom, newPrice);
    }

    public void addRoom(RoomCategory category, RoomStatus status, RoomType type, int roomNumber, int price, int capacity){
        roomManager.addRoom(category, status, type, capacity, roomNumber, price);
    }

    public List<Room> getListAvailableRooms(FilterRoom filter){
        List<Room> availableRooms = roomManager.listAvailableRooms(filter);
        availableRooms.stream().toList().forEach(room -> System.out.println(room.toString()));
        return availableRooms;
    }

    public List<Room> getListAvailableRoomsByDate(FilterRoom filter, LocalDate date){
        List<Room> availableRooms = roomManager.listAvailableRoomsByDate(filter, date);
        availableRooms.stream().toList().forEach(room -> System.out.println(room.toString()));
        return availableRooms;
    }

    public void getInfoAboutRoom(int numberRoom) {
        if(hotel.getRoomMap().get(numberRoom) == null){
            throw new RuntimeException("Нет такого номера!");
        }
        System.out.println(hotel.getRoomMap().get(numberRoom).toString());
    }

    public void requestListRoom(FilterRoom filter){
        roomManager.requestListRoom(filter);
    }

    public void requestListRoomAndPrice(FilterRoom filter){
        roomManager.requestListRoomAndPrice(filter);
    }

    public void addPersonal(Collection<Employee> persons){
        employeeManager.addPersonal(persons);
    }

    public void getInfoAboutClientDatabase(FilterClient filterClient){
        clientManager.getInfoAboutClientDatabase(filterClient);
        System.out.println("Общее число постояльцев: " + hotel.getClientMap().size());
    }

    public void addClientServices(int id, List<Services> list) {
        clientManager.addService(list, id);
    }

    public void getInfoAboutClient(int id) {
        System.out.println(clientManager.getInfoAboutClient(id));
    }

    public void requestLastThreeClient(){
        clientManager.requestLastThreeClient();
    }

    public void requestListServicesClient(FilterServices filter, int idClient){
        clientManager.requestListServicesClient(filter,idClient);
    }

    public void cleaningRequest(int roomId) {
        Room room = hotel.getRoomMap().get(roomId);
        room.setStatus(RoomStatus.CLEANING);
        for (Observer observer: observersMaid){
            if (room != null && room.getStatus()==RoomStatus.CLEANING) {
                observer.update(roomId);
                System.out.printf("%s изменил статус комнаты %d на %s\n",
                        employee.getPosition(), roomId, room.getStatus().getDescription());
            }
        }
    }

    public void repairRequest(int roomId) {
        for (Observer observer: observersMender) {
            Room room = hotel.getRoomMap().get(roomId);
            if (room != null) {
                room.setStatus(RoomStatus.MAINTENANCE);
                System.out.printf("%s изменил статус комнаты %d на %s\n",
                        employee.getPosition(), roomId, room.getStatus().getDescription());
                observer.update(roomId);
            }
        }
    }

    public void settle(Client client, int idRoom, FilterRoom filterRoom) {
        Room room = hotel.getRoomMap().get(idRoom);
        if (room == null || room.getNumber() == 0) {
            System.out.println("Комната " + idRoom + " не существует, мы учтем ваши пожелание и подберем вам номер сами!");
            if (roomManager.listAvailableRoomsByDate(FilterRoom.ID, client.getCheckOutDate()) == null) {
                System.out.printf("Не удалось заселить клиента %s, нет свободных комнат к этой дате\n", client.toString());
                return;
            }
            idRoom = roomManager.listAvailableRoomsByDate(filterRoom, client.getCheckOutDate()).get(0).getNumber();
            client.setIdRoom(idRoom);
            hotel.getClientMap().put(client.getId(), client);
            System.out.printf("%s поселил клиента %s\n", employee.getPosition(), hotel.getClientMap().get(client.getId()).toString() +
                    " в номер:" + hotel.getClientMap().get(client.getId()).getNumberRoom() + "\n");
            hotel.getRoomMap().get(idRoom).setStatus(RoomStatus.OCCUPIED);
            return;
        }

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            System.out.println("Данная комната не обслуживается, выберите другую");
            return;
        }

        hotel.getClientMap().put(client.getId(), client);
        System.out.printf("%s поселил клиента %s\n", employee.getPosition(), hotel.getClientMap().get(client.getId()).toString() +
                " в номер:" + hotel.getClientMap().get(client.getId()).getNumberRoom() + "\n");
        hotel.getRoomMap().get(idRoom).setStatus(RoomStatus.OCCUPIED);
    }

    public void settle(Client client, FilterRoom filterRoom) {
        if (roomManager.listAvailableRoomsByDate(FilterRoom.ID, client.getCheckOutDate()) == null) {
            System.out.printf("Не удалось заселить клиента %s, нет свободных комнат к этой дате\n", client.toString());
            return;
        }
        int idRoom = roomManager.listAvailableRoomsByDate(filterRoom, client.getCheckOutDate()).get(0).getNumber();
        client.setIdRoom(idRoom);
        hotel.getClientMap().put(client.getId(), client);
        System.out.printf("%s поселил клиента %s\n", employee.getPosition(), hotel.getClientMap().get(client.getId()).toString() +
                " в номер:" + hotel.getClientMap().get(client.getId()).getNumberRoom() + "\n");
        hotel.getRoomMap().get(idRoom).setStatus(RoomStatus.OCCUPIED);
        return;
    }

    public void settle(Client client) {
        if (roomManager.listAvailableRoomsByDate(FilterRoom.ID, client.getCheckOutDate()) == null) {
            System.out.printf("Не удалось заселить клиента %s, нет свободных комнат к этой дате\n", client.toString());
            return;
        }
        int idRoom = roomManager.listAvailableRoomsByDate(FilterRoom.ID, client.getCheckOutDate()).get(0).getNumber();
        client.setIdRoom(idRoom);
        hotel.getClientMap().put(client.getId(), client);
        System.out.printf("%s поселил клиента %s\n", employee.getPosition(), hotel.getClientMap().get(client.getId()).toString() +
                " в номер:" + hotel.getClientMap().get(client.getId()).getNumberRoom() + "\n");
        hotel.getRoomMap().get(idRoom).setStatus(RoomStatus.OCCUPIED);
    }

    public void givOutCheck(int idClient){
        int sum = 0;
        Client client = hotel.getClientMap().get(idClient);
        int idRoom = client.getNumberRoom();

        sum += hotel.getRoomMap().get(idRoom).getPrice();
        for(Services service : client.getServicesList()){
            sum += (int) service.getPrice();
        }
        System.out.printf("%s предоставил счет клиенту в размере: %d\n",employee.getPosition(), sum);
    }

    public void evict(int idClient){
        Client client = hotel.getClientMap().get(idClient);
        int roomId = client.getNumberRoom();
        Room room = hotel.getRoomMap().get(roomId);

        if (client == null) {
            return;
        }
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
