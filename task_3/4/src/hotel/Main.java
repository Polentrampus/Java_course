package hotel;

import hotel.model.Hotel;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.personal.client.Client;
import hotel.personal.employee.admin.Admin;
import hotel.personal.employee.reception.Reception;
import hotel.personal.employee.service.Maid;
import hotel.personal.employee.service.Mender;
import hotel.personal.employee.service.ServicePersonnelHotel;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Hotel hotel = Hotel.getInstance();

        Admin admin = new Admin("Вася", "Васин", "Васильевич", 27, 4, 2005);
        Reception reception = new Reception("Иван", "Иванов", "Иванович", 17, 3, 1990);
        ServicePersonnelHotel maid_1 = new Maid("Лена", "Рыжова", "Еленина", 2, 7, 2001);
        ServicePersonnelHotel maid_2 = new Mender("Петя", "Кек", "Кекович", 4, 2, 1998);

        admin.addPersonal(Arrays.asList(reception, maid_1, maid_2));
        admin.addRoom(RoomCategory.ECONOMY, RoomStatus.AVAILABLE, RoomType.STANDARD, 203, 13000);
        admin.addService("Мини бар", "В стоимость номера будет включен мини бар", 100.3);
        admin.changeRoomPrice(100, 2500);
        admin.changeServicePrice("Трансфер", 2000);

        Client client_1 = new Client("Володя","Лол","Лолович", 12, 12, 1987);
        reception.addClient(client_1);
        reception.settle(client_1.getId(), 202);
        reception.settle(client_1.getId(), 203);

        reception.evict(client_1.getId());
        reception.repairRequest(102);

    }
}