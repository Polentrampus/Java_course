package hotel.personal.employee.reception;

import hotel.model.Hotel;
import hotel.model.room.RoomStatus;
import hotel.personal.employee.HotelEmployee;
import hotel.personal.client.Client;

public class Reception extends HotelEmployee {
    private Hotel hotel = Hotel.getInstance();

    public Reception(String name, String surname, String patronymic, int date_of_birth, int month_of_birth, int year_of_birth) {
        super(name, surname, patronymic, date_of_birth, month_of_birth, year_of_birth);
        System.out.println("Вы пригласили ресепшиониста");
    }

    public void setStatusRoom(int roomId, RoomStatus status){
        System.out.printf("%s изменил состояние комнаты %d с %s на %s\n", getPosition(), roomId, hotel.getRoomMap().get(roomId).getStatus(), status);
        hotel.getRoomMap().get(roomId).setStatus(status);
    }

    @Override
    public String getPosition() {
        return "Ресепшионист";
    }

    @Override
    public void addClient(Client person) {
        hotel.getClientMap().put(person.getId(),person);
        System.out.println("Ресепшионист добавил клиента: " + person.toString());
    }

    @Override
    public String toString() {
        return "Reception{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                '}';
    }
}
