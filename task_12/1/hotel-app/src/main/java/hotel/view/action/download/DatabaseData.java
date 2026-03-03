package hotel.view.action.download;

import hotel.repository.RepositoryFactory;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.client.ClientRepository;
import hotel.repository.employee.EmployeeRepository;
import hotel.repository.room.RoomRepository;
import hotel.repository.service.ServicesRepository;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class DatabaseData extends BaseAction {
    private RepositoryFactory repositoryFactory = new RepositoryFactory();;

    public DatabaseData(Scanner scanner) {
        super(scanner);
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== РАБОТА С БД ===");
            repositoryFactory.setDataSourceType(RepositoryFactory.DataSourceType.DATABASE);

            RoomRepository roomRepo = repositoryFactory.createRoomRepository();
            ClientRepository clientRepo = repositoryFactory.createClientRepository();
            EmployeeRepository employeeRepo = repositoryFactory.createEmployeeRepository();
            BookingsRepository bookingsRepo = repositoryFactory.createBookingsRepository();
            ServicesRepository servicesRepository = repositoryFactory.createServiceRepository();

            System.out.println("Можно работать с БД");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
