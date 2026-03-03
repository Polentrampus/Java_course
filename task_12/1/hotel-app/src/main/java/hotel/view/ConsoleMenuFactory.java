package hotel.view;

import hotel.annotation.Component;
import hotel.annotation.Inject;
import hotel.repository.RepositoryFactory;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.client.ClientRepository;
import hotel.repository.employee.EmployeeRepository;
import hotel.repository.room.RoomRepository;
import hotel.repository.service.ServicesRepository;
import hotel.service.ClientService;
import hotel.service.EmployeeService;
import hotel.service.IBookingService;
import hotel.service.IRoomService;
import hotel.service.RoomService;
import hotel.service.ServicesService;
import hotel.view.action.booking.CreateBookingAction;
import hotel.view.action.booking.DeleteBookingByIdAction;
import hotel.view.action.booking.GetAllBookingsAction;
import hotel.view.action.booking.GetBookingByIdAction;
import hotel.view.action.booking.UpdateBookingAction;
import hotel.view.action.client.CheckClientOfHotel;
import hotel.view.action.client.CsvExportClientsAction;
import hotel.view.action.client.CsvImportClientsAction;
import hotel.view.action.client.EvictClientOfHotel;
import hotel.view.action.client.GetInfoAboutClientAction;
import hotel.view.action.client.GetInfoAboutClientDatabaseAction;
import hotel.view.action.client.RequestLastThreeClientAction;
import hotel.view.action.employee.CleaningRequestAction;
import hotel.view.action.employee.CsvExportEmployeeAction;
import hotel.view.action.employee.CsvImportEmployeeActon;
import hotel.view.action.employee.EmployeeListAction;
import hotel.view.action.employee.RepairRequestAction;
import hotel.view.action.room.AddRoomAction;
import hotel.view.action.room.ChangeRoomPriceAction;
import hotel.view.action.room.CsvExportRoomsAction;
import hotel.view.action.room.CsvImportRoomsActon;
import hotel.view.action.room.GetInfoAboutRoomAction;
import hotel.view.action.room.GetListAvailableRoomsAction;
import hotel.view.action.room.GetListAvailableRoomsByDateAction;
import hotel.view.action.room.ReadRoomHistoryAction;
import hotel.view.action.room.RequestListRoomAction;
import hotel.view.action.room.RequestListRoomAndPriceAction;
import hotel.view.action.services.AddServiceAction;
import hotel.view.action.services.ChangePriceServiceAction;
import hotel.view.action.services.CsvExportServicesAction;
import hotel.view.action.services.CsvImportServicesActon;
import hotel.view.action.services.RequestListServicesAction;

import java.io.IOException;
import java.util.Scanner;

@Component("consoleMenuFactory")
public class ConsoleMenuFactory extends BaseMenuFactory {
    @Inject
    private ClientService clientService;
    @Inject
    private IRoomService roomService;
    @Inject
    private IBookingService bookingService;
    @Inject
    private ServicesService serviceService;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private Navigator navigator;
    private final Scanner scanner = new Scanner(System.in);
    private final RepositoryFactory repositoryFactory = new RepositoryFactory();
    private RepositoryFactory.DataSourceType currentDataSourceType = null;

    public ConsoleMenuFactory() {
    }

    @Override
    public Menu downloadMenu() throws IOException {
        Menu downloadMenu = new Menu("Меню загрузки данных");

        downloadMenu.addMenuItem(MenuItem.createMenuItem("Загрузить из файла .json", () -> {
            try {
                loadFromJson();
                navigator.setCurrentMenu(createMainMenu());
            } catch (Exception e) {
                System.out.println("Ошибка загрузки из JSON: " + e.getMessage());
            }
        }));

        downloadMenu.addMenuItem(MenuItem.createMenuItem("Создать тестовые данные", () -> {
            try {
                loadInMemory();
                navigator.setCurrentMenu(createMainMenu());
            } catch (Exception e) {
                System.out.println("Ошибка создания тестовых данных: " + e.getMessage());
            }
        }));

        downloadMenu.addMenuItem(MenuItem.createMenuItem("Работать с БД", () -> {
            try {
                loadFromDatabase();
                navigator.setCurrentMenu(createMainMenu());
            } catch (Exception e) {
                System.out.println("Ошибка подключения к БД: " + e.getMessage());
            }
        }));

        return downloadMenu;
    }

    private void initializeRepositories(RepositoryFactory.DataSourceType dataSourceType) throws Exception {
        this.currentDataSourceType = dataSourceType;

        System.out.println("\n=== ЗАГРУЗКА ДАННЫХ ИЗ: " + dataSourceType + " ===");

        RepositoryFactory repositoryFactory = new RepositoryFactory();
        repositoryFactory.setDataSourceType(dataSourceType);

        RoomRepository roomRepo = repositoryFactory.createRoomRepository();
        ClientRepository clientRepo = repositoryFactory.createClientRepository();
        EmployeeRepository employeeRepo = repositoryFactory.createEmployeeRepository();
        BookingsRepository bookingsRepo = repositoryFactory.createBookingsRepository();
        ServicesRepository servicesRepository = repositoryFactory.createServiceRepository();

        roomService.setHotelRepository(roomRepo, bookingsRepo);
        clientService.setHotelRepository(clientRepo, bookingsRepo);
        employeeService.setHotelRepository(employeeRepo);
        bookingService.setHotelRepository(bookingsRepo, clientRepo, roomRepo);
        serviceService.setHotelRepository(servicesRepository);

        System.out.println("Репозитории инициализированы для: " + dataSourceType);
    }

    private void loadFromJson() throws Exception {
        System.out.println("=== ЗАГРУЗКА ИЗ ФАЙЛА .JSON ===");
        initializeRepositories(RepositoryFactory.DataSourceType.JSON);
        System.out.println("Данные загружены из JSON файла");
    }

    private void loadInMemory() throws Exception {
        System.out.println("=== ЗАГРУЗКА СВОИХ ДАННЫХ ===");
        initializeRepositories(RepositoryFactory.DataSourceType.IN_MEMORY);
        System.out.println("Тестовые данные созданы в памяти");
    }

    private void loadFromDatabase() throws Exception {
        System.out.println("=== РАБОТА С БАЗОЙ ДАННЫХ ===");
        initializeRepositories(RepositoryFactory.DataSourceType.DATABASE);
        System.out.println("Подключение к базе данных установлено");
    }

    @Override
    public Menu createMainMenu() throws IOException {
        Menu mainMenu = new Menu("Главное меню");

        mainMenu.addMenuItem(MenuItem.createNavigatorMenuItem("Клиенты", navigator, createClientMenu()));
        mainMenu.addMenuItem(MenuItem.createNavigatorMenuItem("Комнаты", navigator, createRoomMenu()));
        mainMenu.addMenuItem(MenuItem.createNavigatorMenuItem("Рабочие", navigator, createEmployeeMenu()));
        mainMenu.addMenuItem(MenuItem.createNavigatorMenuItem("Услуги", navigator, createServicesMenu()));
        mainMenu.addMenuItem(MenuItem.createNavigatorMenuItem("Брони", navigator, createBookingMenu()));

        return mainMenu;
    }

    @Override
    public Menu createClientMenu() {
        Menu menu = new Menu("Управление гостями");
        menu.addMenuItem(MenuItem.createMenuItem("Добавить клиента в систему",
                new CheckClientOfHotel(clientService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Удалить клиента из системы",
                new EvictClientOfHotel(clientService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию по клиенту",
                new GetInfoAboutClientAction(clientService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию о базе клиентов",
                new GetInfoAboutClientDatabaseAction(clientService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию о последних трех клиентах",
                new RequestLastThreeClientAction(clientService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Экспортировать данные о клиентах",
                new CsvExportClientsAction(clientService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Импортировать данные о клиентах",
                new CsvImportClientsAction(clientService, scanner)));
        return menu;
    }

    @Override
    public Menu createEmployeeMenu() {
        Menu menu = new Menu("Управление работниками");
        menu.addMenuItem(MenuItem.createMenuItem("Список всех работников",
                new EmployeeListAction(employeeService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Запросить уборку номера",
                new CleaningRequestAction(employeeService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Запросить ремонт номера",
                new RepairRequestAction(employeeService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Экспортировать данные о работниках",
                new CsvExportEmployeeAction(employeeService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Импортировать данные о работниках",
                new CsvImportEmployeeActon(employeeService, scanner)));

        return menu;
    }

    @Override
    public Menu createRoomMenu() {
        Menu menu = new Menu("Управление комнатами");
        menu.addMenuItem(MenuItem.createMenuItem("Добавить новую комнату",
                new AddRoomAction(roomService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Изменить цену комнаты",
                new ChangeRoomPriceAction(roomService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию о комнате",
                new GetInfoAboutRoomAction(roomService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию о свободных номерах",
                new GetListAvailableRoomsAction(roomService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию о свободных номерах к определенной дате",
                new GetListAvailableRoomsByDateAction(roomService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить список комнат",
                new RequestListRoomAction(roomService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить список комнат и их цену",
                new RequestListRoomAndPriceAction(roomService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Экспортировать данные о комнатах",
                new CsvExportRoomsAction(roomService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Импортировать данные о комнатах",
                new CsvImportRoomsActon(roomService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("История комнаты",
                new ReadRoomHistoryAction(bookingService, scanner)));

        return menu;
    }

    @Override
    public Menu createServicesMenu() {
        Menu menu = new Menu("Управление услугами");
        menu.addMenuItem(MenuItem.createMenuItem("Добавить услугу",
                new AddServiceAction(serviceService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Изменить цену услуги",
                new ChangePriceServiceAction(serviceService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию о базе услуг",
                new RequestListServicesAction(serviceService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Экспортировать данные о услугах",
                new CsvExportServicesAction(serviceService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Импортировать данные о услугах",
                new CsvImportServicesActon(serviceService, scanner)));

        return menu;
    }

    @Override
    public Menu createBookingMenu() {
        Menu menu = new Menu("Управление бронями");
        menu.addMenuItem(MenuItem.createMenuItem("Создать бронь",
                new CreateBookingAction(clientService, roomService, bookingService, serviceService, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Удалить бронь",
                new DeleteBookingByIdAction(bookingService, scanner, clientService, roomService)));
        menu.addMenuItem(MenuItem.createMenuItem("Получить список бронирований",
                new GetAllBookingsAction(bookingService, scanner, clientService)));
        menu.addMenuItem(MenuItem.createMenuItem("Получить бронь по id",
                new GetBookingByIdAction(bookingService, scanner, clientService, roomService)));
        menu.addMenuItem(MenuItem.createMenuItem("Изменить бронь по id",
                new UpdateBookingAction(clientService, roomService, bookingService, scanner)));

        return menu;
    }

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public ClientService getClientService() {
        return clientService;
    }

    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    public IBookingService getBookingService() {
        return bookingService;
    }

    public void setBookingService(IBookingService bookingService) {
        this.bookingService = bookingService;
    }

    public ServicesService getServiceService() {
        return serviceService;
    }

    public void setServiceService(ServicesService serviceService) {
        this.serviceService = serviceService;
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public Navigator getNavigator() {
        return navigator;
    }
}