package hotel.util;

import hotel.dto.HotelDto;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.model.users.employee.Employee;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class JsonDataManager {
    private static final String DATA_FILE = "data/hotel_data.json";

    private final Map<Integer, Room> rooms = new ConcurrentHashMap<>();
    private final Map<Integer, Employee> employees = new ConcurrentHashMap<>();
    private final Map<Integer, Client> clients = new ConcurrentHashMap<>();
    private final Map<Integer, Bookings> bookings = new ConcurrentHashMap<>();
    private final Map<Integer, Services> services = new ConcurrentHashMap<>();

    private static JsonDataManager instance;

    private JsonDataManager() {
        loadFromJson();
    }

    public static synchronized JsonDataManager getInstance() {
        if (instance == null) {
            instance = new JsonDataManager();
        }
        return instance;
    }

    private void loadFromJson() {
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            System.out.println("Data file not found, starting with empty data");
            return;
        }

        try {
            HotelDto dto = JsonUtils.getMapper().readValue(file, HotelDto.class);

            if (dto.getRooms() != null) rooms.putAll(dto.getRooms());
            if (dto.getEmployees() != null) employees.putAll(dto.getEmployees());
            if (dto.getClients() != null) clients.putAll(dto.getClients());
            if (dto.getBookings() != null) bookings.putAll(dto.getBookings());
            if (dto.getServices() != null) services.putAll(dto.getServices());

            System.out.println("Loaded data from JSON: " +
                    rooms.size() + " rooms, " +
                    employees.size() + " employees, " +
                    clients.size() + " clients, " +
                    bookings.size() + " bookings, " +
                    services.size() + " services");
        } catch (IOException e) {
            System.err.println("Failed to load data from JSON: " + e.getMessage());
        }
    }

    public void saveToJson() {
        try {
            File file = new File(DATA_FILE);
            file.getParentFile().mkdirs();

            HotelDto dto = new HotelDto(
                    new HashMap<>(rooms),
                    new HashMap<>(employees),
                    new HashMap<>(clients),
                    new HashMap<>(bookings),
                    new HashMap<>(services)
            );

            JsonUtils.getMapper().writeValue(file, dto);
            System.out.println("Saved data to JSON");
        } catch (IOException e) {
            System.err.println("Failed to save data to JSON: " + e.getMessage());
        }
    }

    // Геттеры для коллекций
    public Map<Integer, Room> getRooms() {
        return rooms;
    }

    public Map<Integer, Employee> getEmployees() {
        return employees;
    }

    public Map<Integer, Client> getClients() {
        return clients;
    }

    public Map<Integer, Bookings> getBookings() {
        return bookings;
    }

    public Map<Integer, Services> getServices() {
        return services;
    }

    public void saveRoom(Room room) {
        rooms.put(room.getNumber(), room);
        saveToJson();
    }

    public void deleteRoom(int id) {
        rooms.remove(id);
        saveToJson();
    }

    public void saveClient(Client client) {
        clients.put(client.getId(), client);
        saveToJson();
    }

    public void deleteClient(int id) {
        clients.remove(id);
        saveToJson();
    }

    public void saveEmployee(Employee employee) {
        employees.put(employee.getId(), employee);
        saveToJson();
    }

    public void deleteEmployee(int id) {
        employees.remove(id);
        saveToJson();
    }

    public void saveBooking(Bookings booking) {
        bookings.put(booking.getId(), booking);
        saveToJson();
    }

    public void deleteBooking(int id) {
        bookings.remove(id);
        saveToJson();
    }

    public void saveService(Services service) {
        services.put(service.getId(), service);
        saveToJson();
    }

    public void deleteService(int id) {
        services.remove(id);
        saveToJson();
    }
}