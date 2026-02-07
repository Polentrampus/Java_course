package hotel.view.action.download;

import hotel.RepositoryFactory;

import hotel.repository.booking.BookingsRepository;
import hotel.repository.client.ClientRepository;
import hotel.repository.employee.EmployeeRepository;
import hotel.repository.room.RoomRepository;
import hotel.repository.service.ServicesRepository;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class UploadDataUsingJson extends BaseAction {
    private final RepositoryFactory repositoryFactory = new RepositoryFactory();;

    public UploadDataUsingJson(Scanner scanner) {
        super(scanner);
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ЗАГРУЗИТЬ ИЗ ФАЙЛА .JSON ===");
            repositoryFactory.setDataSourceType(RepositoryFactory.DataSourceType.JSON);

            RoomRepository roomRepo = repositoryFactory.createRoomRepository();
            ClientRepository clientRepo = repositoryFactory.createClientRepository();
            EmployeeRepository employeeRepo = repositoryFactory.createEmployeeRepository();
            BookingsRepository bookingsRepo = repositoryFactory.createBookingsRepository();
            ServicesRepository servicesRepository = repositoryFactory.createServiceRepository();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
