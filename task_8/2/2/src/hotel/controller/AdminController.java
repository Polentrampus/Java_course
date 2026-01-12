package hotel.controller;

import hotel.annotation.Component;
import hotel.annotation.Inject;
import hotel.config.HotelConfiguration;
import hotel.dto.CreateBookingRequest;
import hotel.exception.ConfigException;
import hotel.model.booking.Bookings;
import hotel.service.*;
import hotel.model.Hotel;
import hotel.model.filter.ClientFilter;
import hotel.model.filter.RoomFilter;
import hotel.model.filter.ServicesFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.service.Observer;
import java.time.LocalDate;
import java.util.*;

@Component
public class AdminController {
    private Hotel hotel = Hotel.getInstance();
    @Inject
    private ClientService clientService;
    @Inject
    private ServicesService servicesService;
    @Inject
    private IRoomService roomService;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private HotelConfiguration config;
    @Inject
    private ServiceFactory serviceFactory;
    @Inject
    private IBookingService bookingService;

    private Employee employee;
    private List<Observer> observersMaid = new ArrayList<>();
    private List<Observer> observersMender = new ArrayList<>();

    public AdminController() {
    }

    public void initialize(List<Employee> employees, HotelConfiguration hotelConfiguration, ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        this.roomService = serviceFactory.createRoomService();
        this.config = hotelConfiguration;
        this.bookingService = serviceFactory.createBookingService();

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

    public List<Bookings> getRoomHistory(int roomId) {
        if (canReadBookings()) {
            return getAdvancedBookingService().getRoomBookingHistory(roomId);
        } else {
            System.out.println("Просмотр истории бронирования не доступен!");
            return Collections.emptyList();
        }
    }

    private void addObserverMaid(Observer observer) {
        observersMaid.add(observer);
    }

    private void addObserverMender(Observer observer) {
        observersMender.add(observer);
    }

    public List<Employee> getEmployees() {
        if( hotel.getEmployeeMap().isEmpty()){
            return Collections.emptyList();
        }
        return hotel.getEmployeeMap().get().values().stream().toList();
    }

    private ModifiableIRoomService getModifiableRoomService () {
        if (roomService instanceof ModifiableIRoomService) {
            return (ModifiableIRoomService) roomService;
        }
        throw new ConfigException();
    }

    private boolean canModifyRooms () {
        return config.isRoomStatusModifiable() && roomService instanceof ModifiableIRoomService;
    }

    private AdvancedBookingService getAdvancedBookingService () {
        if (bookingService instanceof AdvancedBookingService) {
            return (AdvancedBookingService) bookingService;
        }
        throw new ConfigException();
    }

    private boolean canReadBookings () {
        return config.isBookingHistoryEnabled() && bookingService instanceof AdvancedBookingService;
    }

    public List<Bookings> findAllBookings() {
        if(canReadBookings())
            return getAdvancedBookingService().getAllBookings();
        else
            throw new ConfigException();
    }

    public Optional<Bookings> createBooking(CreateBookingRequest request) {
        if(canReadBookings())
            return getAdvancedBookingService().createBooking(request);
        else
            throw new ConfigException();
    }

    public List<Bookings> getRoomBookingHistory(int roomId) {
        if(canReadBookings())
            return getAdvancedBookingService().getRoomBookingHistory(roomId);
        else
            throw new ConfigException();
    }

    public Optional<Bookings> getBookingById(Integer id) {
        return bookingService.getBookingById(id);
    }

    public void changeRoomPrice ( int idRoom, int newPrice){
        if (canModifyRooms()) {
            getModifiableRoomService().changeRoomPrice(idRoom, newPrice);
        } else {
            throw new ConfigException();
        }
    }

    public void addRoom(RoomCategory category, RoomStatus status, RoomType type,
                        int roomNumber, int price, int capacity) {
        if (canModifyRooms()) {
            getModifiableRoomService().addRoom(category, status, type, roomNumber, price, capacity);
        } else {
            throw new ConfigException();
        }
    }

    public List<Room> getListAvailableRooms(RoomFilter filter) {
        List<Room> availableRooms = roomService.listAvailableRooms(filter);
        System.out.println("список свободных комнат: ");
        availableRooms.stream().toList().forEach(room -> System.out.println(room.toString()));
        return availableRooms;
    }

    public List<Room> getListAvailableRoomsByDate(RoomFilter filter, LocalDate date) {
        List<Room> availableRooms = roomService.listAvailableRoomsByDate(filter, date);
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

    public List<Room> requestListRoom(RoomFilter filter) {
        return roomService.requestListRoom(filter);
    }

    public void requestListRoomAndPrice(RoomFilter filter) {
        roomService.requestListRoomAndPrice(filter);
    }

    public void addPersonal(Collection<? extends Employee> persons) {
        employeeService.addPersonal(persons);
    }

    public Collection<Client> getInfoAboutClientDatabase(ClientFilter clientFilter) {
        clientService.getInfoAboutClientDatabase(clientFilter);
        System.out.println("Общее число постояльцев: " + hotel.getClientMap().get().size());
        return hotel.getClientMap().get().values();
    }

    public void addClientServices(int id, List<Services> list) {
        clientService.addService(list, id);
        System.out.println("Добавление услуг: клиенту с ID: " + id + " услуги:\n" + clientService.getServices(id));
    }

    public void getInfoAboutClient(int id) {
        System.out.println(clientService.getInfoAboutClient(id));
    }

    public void requestLastThreeClient() {
        clientService.requestLastThreeClient();
    }

    public void requestListServicesClient(ServicesFilter filter, int idClient) {
        clientService.requestListServicesClient(filter, idClient);
    }

    public Collection<Services> requestListServices() {
        System.out.println("Запрошен список услуг:");
        return servicesService.requestListServices();
    }

    public void addService(int id, String name, String description, int price) {
        servicesService.addService(id, name, description, price);
    }

    public void changePriceService(String name, int price) {
        servicesService.setPrice(name, price);
    }

    public void cleaningRequest(int roomId) {
        Room room = hotel.getRoomMap().get().get(roomId);
        room.setStatus(RoomStatus.CLEANING);
        for (Observer observer : observersMaid) {
            if (room != null && room.getStatus() == RoomStatus.CLEANING) {
                observer.update(roomId);
                System.out.printf("изменился статус комнаты %d на %s\n", roomId, room.getStatus().getDescription());
            }
        }
    }

    public void repairRequest(int roomId) {
        for (Observer observer : observersMender) {
            Room room = hotel.getRoomMap().get().get(roomId);
            if (room != null) {
                room.setStatus(RoomStatus.MAINTENANCE);
                System.out.printf("Изменился статус комнаты %d на %s\n", roomId, room.getStatus().getDescription());
                observer.update(roomId);
            }
        }
    }

    public void settle(Client client, LocalDate checkOutDate) {
        if (roomService.listAvailableRoomsByDate(RoomFilter.ID, client.getCheckOutDate()) == null) {
            System.out.printf("Не удалось заселить клиента %s, нет свободных комнат к этой дате\n", client.toString());
            return;
        }
        int idRoom = roomService.listAvailableRoomsByDate(RoomFilter.ID, client.getCheckOutDate()).get(0).getNumber();
        client.setNumberRoom(idRoom);
        client.setCheckOutDate(checkOutDate);
        hotel.getClientMap().get().put(client.getId(), client);
        System.out.printf("%s поселил клиента %s\n", employee.getPosition(), hotel.getClientMap().get().get(client.getId()).toString() +
                " в номер:" + hotel.getClientMap().get().get(client.getId()).getNumberRoom() + "\n");
        hotel.getRoomMap().get().get(idRoom).setStatus(RoomStatus.OCCUPIED);
        bookingService.createBooking(new CreateBookingRequest(client.getId(), idRoom, LocalDate.now(), checkOutDate));
    }

    public void givOutCheck(int idClient) {
        int sum = 0;
        Client client = hotel.getClientMap().get().get(idClient);
        int idRoom = client.getNumberRoom();

        sum += hotel.getRoomMap().get().get(idRoom).getPrice();
        for (Services service : client.getServicesList()) {
            sum += (int) service.getPrice();
        }
        System.out.printf("Предоставлен счет клиенту в размере: %d\n", sum);
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
        client.setNumberRoom(0);
        hotel.getClientMap().get().remove(client.getId());
        cleaningRequest(roomId);
    }

    public Optional<Bookings> updateBooking(CreateBookingRequest request, int bookingId) {
        return bookingService.updateBooking(request,bookingId);
    }

    public void deleteBooking(int id) {
        bookingService.deleteBookingById(id);
    }
}
