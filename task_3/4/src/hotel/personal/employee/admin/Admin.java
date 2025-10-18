package hotel.personal.employee.admin;

import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.model.service.Services;
import hotel.personal.employee.HotelEmployee;
import hotel.personal.Person;

import java.util.Arrays;
import java.util.Collection;

public class Admin extends HotelEmployee {
    public Admin(String name, String surname, String patronymic, int date_of_birth, int month_of_birth, int year_of_birth) {
        super(name, surname, patronymic, date_of_birth, month_of_birth, year_of_birth);
        System.out.println("Вы пригласили администратора");
        this.addPersonal(Arrays.asList(this));
    }

    public void changeRoomPrice(int idRoom, int newPrice){
        System.out.println("Админ изменил цену комнаты номер: " + idRoom +
                " \nс "+hotel.getRoomMap().get(idRoom).getPrice() + " на " + newPrice);
        hotel.getRoomMap().get(idRoom).setPrice(newPrice);
    }

    public void changeServicePrice(String nameService, int newPrice){
        System.out.println("Админ изменил цену услуги: " + nameService +
                " \nс "+ Services.findService(nameService).getPrice() + " на " + newPrice);
        Services.findService(nameService).setPrice(newPrice);
    }

    public void addRoom(RoomCategory category, RoomStatus status, RoomType type, int roomIndex, int price){
        hotel.getRoomMap().put(roomIndex, new Room(category, status, type, roomIndex, price));
        System.out.println("Админ добавил новую комнату: " + hotel.getRoomMap().get(roomIndex).toString());
    }

    public void addService(int idClient, String name_service){
        hotel.getClientMap().get(idClient).addService(name_service);
        System.out.println("Админ добавил услугу клиенту " + hotel.getClientMap().get(idClient).toString() + Services.findService(name_service).getDescription());
    }

    public void addService(String nameService, String description, double price){
        Services.addService(nameService, description, price);
        System.out.println("Админ добавил новый вид услуги: " + Services.findService(nameService).getName());
    }

    public void addPersonal(Collection<Person> persons){
        for(Person person : persons){
            hotel.getPersonalMap().put(person.getId(), person);
            System.out.println("Добавили нового члена команды: " + person.toString());
        }
    }

    @Override
    public String getPosition() {
        return "Админ";
    }


    @Override
    public String toString() {
        return "Admin{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                '}';
    }

}
