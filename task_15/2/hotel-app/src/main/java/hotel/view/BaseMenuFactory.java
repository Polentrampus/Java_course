package hotel.view;

import java.io.IOException;

public abstract class BaseMenuFactory {
    public abstract Menu createMainMenu();
    public abstract Menu createClientMenu();
    public abstract Menu createEmployeeMenu();
    public abstract Menu createRoomMenu();
    public abstract Menu createServicesMenu();
    public abstract Menu createBookingMenu();
}