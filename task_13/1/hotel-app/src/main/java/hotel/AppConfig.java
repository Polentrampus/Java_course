package hotel;

import hotel.config.PropertiesConfiguration;
import hotel.di.DIContainer;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.booking.HibernateBookingsRepository;
import hotel.repository.client.ClientRepository;
import hotel.repository.client.HibernateClientRepository;
import hotel.repository.employee.EmployeeRepository;
import hotel.repository.employee.HibernateEmployeeRepository;
import hotel.repository.room.HibernateRoomRepository;
import hotel.repository.room.RoomRepository;
import hotel.repository.service.HibernateServiceRepository;
import hotel.repository.service.ServicesRepository;
import hotel.service.AdvancedBookingService;
import hotel.service.BookingService;
import hotel.service.ClientService;
import hotel.service.EmployeeObserverService;
import hotel.service.EmployeeService;
import hotel.service.IBookingService;
import hotel.service.IRoomService;
import hotel.service.ModifiableRoomService;
import hotel.service.ReadRoomService;
import hotel.service.ServicesService;
import hotel.service.TransactionManager;
import hotel.view.ConsoleMenuFactory;
import hotel.view.Navigator;

public class AppConfig {
    public static DIContainer configureContainer() {
        DIContainer container = new DIContainer();
        container.registerSingletonType(PropertiesConfiguration.class);
        TransactionManager transactionManager = new TransactionManager();
        container.registerSingleton(TransactionManager.class, transactionManager);

        registerInterfaceImplementations(container);
        registerAllSingletons(container);
        return container;
    }

    private static void registerInterfaceImplementations(DIContainer container) {
        container.registerImplementation(IRoomService.class, ModifiableRoomService.class);
        container.registerImplementation(IBookingService.class, AdvancedBookingService.class);

        container.registerImplementation(RoomRepository.class, HibernateRoomRepository.class);
        container.registerImplementation(ClientRepository.class, HibernateClientRepository.class);
        container.registerImplementation(EmployeeRepository.class, HibernateEmployeeRepository.class);
        container.registerImplementation(ServicesRepository.class, HibernateServiceRepository.class);
        container.registerImplementation(BookingsRepository.class, HibernateBookingsRepository.class);
    }

    private static void registerAllSingletons(DIContainer container) {
        container.registerSingletonType(BookingService.class);
        container.registerSingletonType(AdvancedBookingService.class);
        container.registerSingletonType(ReadRoomService.class);
        container.registerSingletonType(ModifiableRoomService.class);
        container.registerSingletonType(ClientService.class);
        container.registerSingletonType(EmployeeObserverService.class);
        container.registerSingletonType(EmployeeService.class);
        container.registerSingletonType(ServicesService.class);

        container.registerSingletonType(HibernateRoomRepository.class);
        container.registerSingletonType(HibernateClientRepository.class);
        container.registerSingletonType(HibernateEmployeeRepository.class);
        container.registerSingletonType(HibernateServiceRepository.class);
        container.registerSingletonType(HibernateBookingsRepository.class);

        container.registerSingletonType(ConsoleMenuFactory.class);
        container.registerSingletonType(Navigator.class);
    }
}