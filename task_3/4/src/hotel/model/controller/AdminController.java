package hotel.model.controller;

import hotel.model.Hotel;
import hotel.model.controller.manager.ClientManager;
import hotel.model.controller.manager.RoomManager;
import hotel.model.controller.manager.ServicesManager;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.model.service.Services;
import hotel.personal.client.Client;
import hotel.personal.employee.Employee;
import hotel.personal.employee.service.Observer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class AdminController  {
    protected final Hotel hotel = Hotel.getInstance();
    protected final ClientManager clientManager;
    protected final ServicesManager servicesManager;
    protected final RoomManager roomManager;
    protected Employee employee;
    private List<Observer> observersMaid = new ArrayList<>();
    private List<Observer> observersMender = new ArrayList<>();

    public AdminController(ClientManager clientManager, ServicesManager servicesManager, List<Employee> employees, RoomManager roomManager) {
        this.clientManager = clientManager;
        this.servicesManager = servicesManager;
        this.roomManager = roomManager;

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
        System.out.println("Админ изменил цену комнаты номер: " + idRoom +
                " \nс "+hotel.getRoomMap().get(idRoom).getPrice() + " на " + newPrice);
        hotel.getRoomMap().get(idRoom).setPrice(newPrice);
    }

    public void changeServicePrice(String nameService, int newPrice){
        System.out.println("Админ изменил цену услуги: " + nameService +
                " \nс "+ hotel.getSERVICES().get(nameService).getPrice() + " на " + newPrice);
        hotel.getSERVICES().get(nameService).setPrice(newPrice);
    }

    public void addRoom(RoomCategory category, RoomStatus status, RoomType type, int roomIndex, int price){
        hotel.getRoomMap().put(roomIndex, new Room(category, status, type, roomIndex, price));
        System.out.println("Админ добавил новую комнату: " + hotel.getRoomMap().get(roomIndex).toString());
    }

    public void addService(int idClient, List<Services> name_service){
        clientManager.addService(name_service, idClient);
        System.out.println("Админ добавил услугу(и) клиенту " + clientManager.getInfoAboutClient(idClient));
    }

    public void addPersonal(Collection<Employee> persons){
        for(Employee person : persons){
            hotel.getEmployeeMap().put(person.getId(), person);
            System.out.println("Добавили нового члена команды: " + person.toString());
        }
    }

    public void cleaningRequest(int roomId) {
        //пока многопоточки нет, выглядит бредово, но потом можно реализовать как очередь с приоритетом
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

    public void settle(Client client, int idRoom) {
        Room room = hotel.getRoomMap().get(idRoom);
        if (room == null || room.getId() == 0) {
            System.out.println("Комната " + idRoom + " не существует, мы учтем ваши пожелание и подберем вам номер сами!");
            Optional<List<Room>> rooms_available = Optional.ofNullable(roomManager.listAvailableRooms());
            if(rooms_available.isPresent() && !rooms_available.get().isEmpty()) {
                client.setIdRoom(rooms_available.get().getFirst().getId());
                hotel.getClientMap().put(client.getId(), client);
                System.out.printf("%s поселил клиента %s\n", employee.getPosition(), hotel.getClientMap().get(client.getId()).toString() +
                        " в номер:" + hotel.getClientMap().get(client.getId()).getIdRoom() + "\n");
                hotel.getRoomMap().get(rooms_available.get().getFirst().getId()).setStatus(RoomStatus.OCCUPIED);
                return;
            } else {
                throw new RuntimeException("Все комнаты заняты, бай");
            }
        }

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            System.out.println("Данная комната не обслуживается, выберите другую");
            return;
        }

        hotel.getClientMap().put(client.getId(), client);
        System.out.printf("%s поселил клиента %s\n", employee.getPosition(), hotel.getClientMap().get(client.getId()).toString() +
                " в номер:" + hotel.getClientMap().get(client.getId()).getIdRoom() + "\n");
        hotel.getRoomMap().get(idRoom).setStatus(RoomStatus.OCCUPIED);
    }

    public void givOutCheck(int idClient){
        int sum = 0;
        Client client = hotel.getClientMap().get(idClient);
        int idRoom = client.getIdRoom();

        sum += hotel.getRoomMap().get(idRoom).getPrice();
        for(Services service : client.getServicesList()){
            sum += (int) service.getPrice();
        }
        System.out.printf("%s предоставил счет клиенту в размере: %d\n",employee.getPosition(), sum);
    }

    public void evict(int idClient){
        Client client = hotel.getClientMap().get(idClient);
        int roomId = client.getIdRoom();
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

    public void addClientServices(int id, List<Services> list) {
        clientManager.addService(list, id);
    }

    public void getInfoAboutClient(int id) {
        System.out.println(clientManager.getInfoAboutClient(id));
    }
}
