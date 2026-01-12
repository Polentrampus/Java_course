package hotel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.users.client.Client;
import hotel.model.users.employee.Employee;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelDto {
    private Map<Integer, Room> rooms;
    private Map<Integer, Employee> employees;
    private Map<Integer, Client> clients;
    private Map<Integer, Bookings> bookings;

    public HotelDto(Map<Integer, Room> rooms, Map<Integer, Employee> employees,
                    Map<Integer, Client> clients, Map<Integer, Bookings> bookings) {
        this.rooms = rooms;
        this.employees = employees;
        this.clients = clients;
        this.bookings = bookings;
    }

    public HotelDto() {
    }

    public Map<Integer, Room> getRooms() {
        return rooms;
    }

    public void setRooms(Map<Integer, Room> rooms) {
        this.rooms = rooms;
    }

    public Map<Integer, Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Map<Integer, Employee> employees) {
        this.employees = employees;
    }

    public Map<Integer, Client> getClients() {
        return clients;
    }

    public void setClients(Map<Integer, Client> clients) {
        this.clients = clients;
    }

    public Map<Integer, Bookings> getBookings() {
        return bookings;
    }

    public void setBookings(Map<Integer, Bookings> bookings) {
        this.bookings = bookings;
    }
}
