package hotel.model;

import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.model.service.Services;
import hotel.personal.client.Client;
import hotel.personal.employee.Employee;
import hotel.personal.employee.Person;
import hotel.personal.employee.service.Maid;
import hotel.personal.employee.service.Mender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hotel {
    private static final Hotel instance = new Hotel();

    private Hotel() {
        System.out.println("Здравствуйте, это отель ***!");
    }

    public static Hotel getInstance() {
        return instance;
    }

    private static final Map<Integer, Room> roomMap = new HashMap<>();
    private final Map<Integer, Employee> employeMap = new HashMap<>();
    private final Map<Integer, Client> clientMap = new HashMap<>();
    private final Map<String, Services> SERVICES = new HashMap<>();

    public List<Maid> getAvailableMaids() {
        List<Maid> maids = new ArrayList<>();
        for (Person person : employeMap.values()) {
            if (person instanceof Maid) {
                maids.add((Maid) person);
            }
        }
        return maids;
    }

    public List<Mender> getAvailableMender() {
        List<Mender> menders = new ArrayList<>();
        for (Person person : employeMap.values()) {
            if (person instanceof Mender) {
                menders.add((Mender) person);
            }
        }
        return menders;
    }

    static {
        roomMap.put(100, new Room(RoomCategory.ECONOMY, RoomStatus.AVAILABLE, RoomType.SUITE, 100, 5000));
        roomMap.put(101, new Room(RoomCategory.BUSINESS, RoomStatus.AVAILABLE, RoomType.STANDARD, 101, 4000));
        roomMap.put(102, new Room(RoomCategory.PREMIUM, RoomStatus.AVAILABLE, RoomType.FAMILY, 102, 7000));
        roomMap.put(200, new Room(RoomCategory.ECONOMY, RoomStatus.AVAILABLE, RoomType.APARTMENT, 200, 5000));
        roomMap.put(201, new Room(RoomCategory.PREMIUM, RoomStatus.MAINTENANCE, RoomType.STANDARD, 201, 24000));
        roomMap.put(202, new Room(RoomCategory.BUSINESS, RoomStatus.OCCUPIED, RoomType.PRESIDENTIAL, 202, 21000));
        roomMap.put(302, new Room(RoomCategory.PREMIUM, RoomStatus.AVAILABLE, RoomType.FAMILY, 102, 7000));
        roomMap.put(300, new Room(RoomCategory.ECONOMY, RoomStatus.AVAILABLE, RoomType.APARTMENT, 200, 5000));
        roomMap.put(301, new Room(RoomCategory.PREMIUM, RoomStatus.AVAILABLE, RoomType.STANDARD, 201, 24000));
        roomMap.put(303, new Room(RoomCategory.BUSINESS, RoomStatus.AVAILABLE, RoomType.PRESIDENTIAL, 202, 21000));
    }

    public Map<Integer, Room> getRoomMap() {
        return roomMap;
    }

    public Map<Integer, Client> getClientMap() {
        return clientMap;
    }

    public Map<Integer, Employee> getEmployeeMap() {
        return employeMap;
    }

    public Map<String, Services> getSERVICES() {
        return SERVICES;
    }
}

