package hotel.model;

import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.model.users.employee.Employee;
import lombok.Getter;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Hotel {
    @Getter
    private static final Hotel instance = new Hotel();

    private Hotel() {
        System.out.println("Здравствуйте, это отель ***!");
    }

    private static final Map<Integer, Room> roomMap = new HashMap<>();
    private final Map<Integer, Employee> employeMap = new HashMap<>();
    private final Map<Integer, Client> clientMap = new HashMap<>();
    private static final Map<String, Services> services = new HashMap<>();
    private final Map<Integer, Bookings>  bookingsMap = new HashMap<>();

    static {
        services.put("BAGGAGE_STORAGE", new Services(1, "BAGGAGE_STORAGE", "Услуга хранения вашего багажа", 500.0));
        services.put("FOOD_DELIVERY", new Services(2, "FOOD_DELIVERY", "Доставка еды в номер", 1200.0));
        services.put("TRANSFER", new Services(3, "TRANSFER", "Трансфер из/в аэропорт", 2500.0));
        services.put("FITNESS_CENTER", new Services(4, "FITNESS_CENTER", "Доступ в фитнес-центр", 800.0));
        services.put("MINI_BAR", new Services(5, "MINI_BAR", "В стоимость номера будет включен мини бар", 100.3));
        services.put("SPA", new Services(6, "SPA", "Возможность посетить спа", 0.0));
        services.put("CONCIERGE", new Services(7, "CONCIERGE", "Консьерж-сервис", 10.0));

        roomMap.put(100, new Room(100, RoomCategory.ECONOMY, RoomStatus.AVAILABLE, RoomType.SUITE, 5000, 2));
        roomMap.put(101, new Room(101, RoomCategory.BUSINESS, RoomStatus.AVAILABLE, RoomType.STANDARD,4000, 3));
        roomMap.put(102, new Room(102,RoomCategory.PREMIUM, RoomStatus.OCCUPIED, RoomType.FAMILY,7000, 3));
        roomMap.put(200, new Room(200,RoomCategory.ECONOMY, RoomStatus.MAINTENANCE, RoomType.APARTMENT, 5000, 1));
        roomMap.put(201, new Room(201,RoomCategory.PREMIUM, RoomStatus.AVAILABLE, RoomType.STANDARD,24000, 2));
        roomMap.put(202, new Room(202,RoomCategory.BUSINESS, RoomStatus.OCCUPIED, RoomType.PRESIDENTIAL,21000, 2));
        roomMap.put(302, new Room(302,RoomCategory.PREMIUM, RoomStatus.AVAILABLE, RoomType.FAMILY, 7000, 1));
        roomMap.put(300, new Room(300,RoomCategory.ECONOMY, RoomStatus.AVAILABLE, RoomType.APARTMENT,5000, 2));
        roomMap.put(301, new Room(301,RoomCategory.PREMIUM, RoomStatus.OCCUPIED, RoomType.STANDARD,24000, 4));
        roomMap.put(303, new Room(303,RoomCategory.BUSINESS, RoomStatus.AVAILABLE, RoomType.PRESIDENTIAL, 21000, 6));
    }

    public Optional<Map<Integer, Room>> getRoomMap() {
        return Optional.ofNullable(roomMap);
    }

    public Optional<Room> getRoom(Integer roomKey) {
        return Optional.of(roomMap.get(roomKey));
    }

    public List<Room> getRoomByKeys(Integer... keys) {
        return Arrays.stream(keys)
                .map(roomMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<Map<Integer, Client>> getClientMap() {
        return Optional.of(clientMap);
    }

    public Optional<Client> getClient(Integer clientKey) {
        return Optional.ofNullable(clientMap.get(clientKey));
    }

    public List<Client> getClientByKeys(Integer... keys) {
        return Arrays.stream(keys)
                .map(clientMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<Map<Integer, Employee>> getEmployeeMap() {
        return Optional.of(employeMap);
    }

    public Optional<Employee> getEmployee(Integer employeeKey) {
        return Optional.ofNullable(employeMap.get(employeeKey));
    }

    public List<Employee> getEmployeeByKeys(Integer... keys) {
        return Arrays.stream(keys)
                .map(employeMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<Map<String, Services>> getServices() {
        return Optional.ofNullable(services);
    }

    public Optional<Services> getService(String serviceKey) {
        return Optional.of(services.get(serviceKey));
    }

    public List<Services> getServicesByKeys(String... keys) {
        return Arrays.stream(keys)
                .map(services::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<Map<Integer, Bookings>> getBookingsMap() {return Optional.of(bookingsMap);}

    public Optional<Bookings> getBookingsActive(Integer roomNumber, LocalDate dateNow) {
        for (Bookings booking : bookingsMap.values()) {
            if (booking.getRoom().equals(roomNumber) && (dateNow.isAfter(booking.getCheckInDate())) && (dateNow.isBefore(booking.getCheckOutDate()))) {
                return Optional.of(booking);    // возвращаем только активные записи где
                                                // date принадлежит интервалу бронирования
            }
        }
        return Optional.empty();
    }

    public Optional<Bookings> getBookings(Integer roomNumber) {
        for (Bookings booking : bookingsMap.values()) {
            if (booking.getRoom().equals(roomNumber)) {
                return Optional.of(booking);
            }
        }
        return Optional.empty();
    }

}

