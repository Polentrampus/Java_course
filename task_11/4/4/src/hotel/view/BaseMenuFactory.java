package hotel.view;

import hotel.RepositoryFactory;

import java.io.IOException;

public abstract class BaseMenuFactory {
    public abstract Menu downloadMenu() throws IOException;
    public abstract Menu createMainMenu() throws IOException;
    public abstract Menu createClientMenu();
    public abstract Menu createEmployeeMenu();
    public abstract Menu createRoomMenu();
    public abstract Menu createServicesMenu();
    public abstract Menu createBookingMenu();
}