package hotel;

import hotel.exception.RepositoryException;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.booking.InMemoryBookingsRepository;
import hotel.repository.booking.JdbcBookingsRepository;
import hotel.repository.booking.JsonBookingsRepository;
import hotel.repository.client.ClientRepository;
import hotel.repository.client.InMemoryClientRepository;
import hotel.repository.client.JdbcClientRepository;
import hotel.repository.client.JsonClientRepository;
import hotel.repository.employee.EmployeeRepository;
import hotel.repository.employee.InMemoryEmployeeRepository;
import hotel.repository.employee.JdbcEmployeeRepository;
import hotel.repository.employee.JsonEmployeeRepository;
import hotel.repository.room.InMemoryRoomRepository;
import hotel.repository.room.JdbcRoomRepository;
import hotel.repository.room.JsonRoomRepository;
import hotel.repository.room.RoomRepository;
import hotel.repository.service.InMemoryServiceRepository;
import hotel.repository.service.JdbcServiceRepository;
import hotel.repository.service.JsonServiceRepository;
import hotel.repository.service.ServicesRepository;
import java.io.IOException;

public class RepositoryFactory {
    public enum DataSourceType {
        JSON, IN_MEMORY, DATABASE;
    }
    private DataSourceType currentType = DataSourceType.DATABASE;

    public void setDataSourceType(DataSourceType type) {
        this.currentType = type;
    }

    public RoomRepository createRoomRepository() throws IOException {
        return switch (currentType) {
            case JSON -> new JsonRoomRepository();
            case IN_MEMORY -> new InMemoryRoomRepository();
            case DATABASE -> new JdbcRoomRepository();
            default -> throw new RepositoryException( "Unknown data source type");
        };
    }

    public ClientRepository createClientRepository() throws IOException {
        return switch (currentType) {
            case JSON -> new JsonClientRepository();
            case IN_MEMORY -> new InMemoryClientRepository();
            case DATABASE -> new JdbcClientRepository();
            default -> throw new RepositoryException("Unknown data source type");
        };
    }

    public EmployeeRepository createEmployeeRepository() throws IOException {
        return switch (currentType) {
            case JSON -> new JsonEmployeeRepository();
            case IN_MEMORY -> new InMemoryEmployeeRepository();
            case DATABASE -> new JdbcEmployeeRepository();
            default -> throw new RepositoryException("Unknown data source type");
        };
    }

    public BookingsRepository createBookingsRepository() throws IOException {
        return switch (currentType) {
            case JSON -> new JsonBookingsRepository();
            case IN_MEMORY -> new InMemoryBookingsRepository();
            case DATABASE -> new JdbcBookingsRepository();
            default -> throw new RepositoryException("Unknown data source type");
        };
    }

    public ServicesRepository createServiceRepository() {
        switch (currentType) {
            case JSON:
                try {
                    return new JsonServiceRepository( );
                } catch (Exception e) {
                    throw new RepositoryException("Failed to create JSON repository", e);
                }
            case IN_MEMORY:
                return new InMemoryServiceRepository();
            case DATABASE:
                return new JdbcServiceRepository();
            default:
                throw new RepositoryException("Unknown data source type");
        }
    }
}
