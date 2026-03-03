package hotel.view.action.download;

import hotel.RepositoryFactory;

import hotel.repository.booking.BookingsRepository;
import hotel.repository.client.ClientRepository;
import hotel.repository.employee.EmployeeRepository;
import hotel.repository.room.RoomRepository;
import hotel.repository.service.ServicesRepository;
import hotel.view.action.BaseAction;

import java.util.*;

public class CreateTestData extends BaseAction {
    private RepositoryFactory repositoryFactory = new RepositoryFactory();;

    public CreateTestData( Scanner scanner) {
        super(scanner);
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== СОЗДАТЬ ТЕСТОВЫЕ ДАННЫЕ ===");
            repositoryFactory.setDataSourceType(RepositoryFactory.DataSourceType.IN_MEMORY);

            RoomRepository roomRepo = repositoryFactory.createRoomRepository();
            ClientRepository clientRepo = repositoryFactory.createClientRepository();
            EmployeeRepository employeeRepo = repositoryFactory.createEmployeeRepository();
            BookingsRepository bookingsRepo = repositoryFactory.createBookingsRepository();
            ServicesRepository servicesRepository = repositoryFactory.createServiceRepository();

            System.out.println("Тестовые данные созданы");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
