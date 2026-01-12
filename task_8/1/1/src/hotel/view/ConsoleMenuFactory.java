package hotel.view;

import hotel.controller.AdminController;
import hotel.view.action.booking.*;
import hotel.view.action.client.CheckClientOfHotel;
import hotel.view.action.client.EvictClientOfHotel;
import hotel.view.action.client.*;
import hotel.view.action.employee.CleaningRequestAction;
import hotel.view.action.employee.CsvExportEmployeeAction;
import hotel.view.action.employee.CsvImportEmployeeActon;
import hotel.view.action.employee.RepairRequestAction;
import hotel.view.action.room.*;
import hotel.view.action.services.*;

import java.util.Scanner;

public class ConsoleMenuFactory extends BaseMenuFactory {
    private final AdminController admin;
    private final Navigator navigator;
    private Scanner scanner;

    public ConsoleMenuFactory(AdminController admin, Navigator navigator) {
        this.admin = admin;
        this.navigator = navigator;
        scanner = new Scanner(System.in);
    }

    @Override
    public Menu createMainMenu() {
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
        menu.addMenuItem(MenuItem.createMenuItem("Заселить клиента", new CheckClientOfHotel(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Выселить клиента", new EvictClientOfHotel(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Добавить услуги(у) клиенту", new AddClientServicesAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию по клиенту", new GetInfoAboutClientAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию о базе клиентов", new GetInfoAboutClientDatabaseAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию о последних трех клиентах", new RequestLastThreeClientAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить список услуг клиента", new RequestListServicesClientAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Экспортировать данные о клиентах", new CsvExportClientsAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Импортировать данные о клиентах", new CsvImportClientsAction(admin, scanner)));


        return menu;
    }

    @Override
    public Menu createEmployeeMenu() {
        Menu menu = new Menu("Управление работниками");
        menu.addMenuItem(MenuItem.createMenuItem("Запросить уборку номера", new CleaningRequestAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Запросить ремонт номера", new RepairRequestAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Экспортировать данные о работниках", new CsvExportEmployeeAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Импортировать данные о работниках", new CsvImportEmployeeActon(scanner, admin)));


        return menu;
    }

    @Override
    public Menu createRoomMenu() {
        Menu menu = new Menu("Управление комнатами");
        menu.addMenuItem(MenuItem.createMenuItem("Добавить новую комнату", new AddRoomAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Изменить цену комнаты", new ChangeRoomPriceAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию о комнате", new GetInfoAboutRoomAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию о свободных номерах", new GetListAvailableRoomsAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию о свободных номерах к определенной дате", new GetListAvailableRoomsByDateAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить список комнат", new RequestListRoomAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить список комнат и их цену", new RequestListRoomAndPriceAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Экспортировать данные о комнатах", new CsvExportRoomsAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Импортировать данные о комнатах", new CsvImportRoomsActon(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("История комнаты", new ReadRoomHistoryAction(admin, scanner)));

        return menu;
    }

    @Override
    public Menu createServicesMenu() {
        Menu menu = new Menu("Управление услугами");
        menu.addMenuItem(MenuItem.createMenuItem("Добавить услугу", new AddServiceAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Изменить цену услуги", new ChangePriceServiceAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Предоставить информацию о базе услуг", new RequestListServicesAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Экспортировать данные о услугах", new CsvExportServicesAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Импортировать данные о услугах", new CsvImportServicesActon(admin, scanner)));
        return menu;
    }

    @Override
    public Menu createBookingMenu() {
        Menu menu = new Menu("Управление бронями");
        menu.addMenuItem(MenuItem.createMenuItem("Создать бронь", new CreateBookingAction(admin, scanner)));
        menu.addMenuItem(MenuItem.createMenuItem("Удалить бронь", new DeleteBookingByIdAction(scanner, admin)));
        menu.addMenuItem(MenuItem.createMenuItem("Получить список бронирований", new GetAllBookingsAction(scanner, admin)));
        menu.addMenuItem(MenuItem.createMenuItem("Получить бронь по id", new GetBookingByIdAction(scanner, admin)));
        menu.addMenuItem(MenuItem.createMenuItem("Изменить бронь по id", new UpdateBookingAction(scanner, admin)));
        return menu;
    }


}
