package hotel.model.users.employee.service;

import hotel.model.Hotel;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.model.users.employee.Employee;


import java.time.LocalDate;

public class Mender extends Employee implements Observer {
    private boolean isFixing = false;

    public Mender(int id, String name, String surname, String patronymic, LocalDate date_of_birth) {
        super(id, name, surname, patronymic, date_of_birth);
        System.out.println("Вы пригласили мастера" );
    }

    @Override
    public void update(int roomId) {
        this.isFixing = true;
        System.out.printf("Мастер %s получил запрос на обслуживание номера %d\n",
                toString(), roomId);
        fixTheRoom(roomId);
    }

    private void fixTheRoom(int roomId) {
        System.out.printf("Мастер %s начал решать проблему номера %d\n",
                toString(), roomId);
        System.out.printf("Мастер %s закончил решать проблему номера %d\n",
                toString(), roomId);
        this.isFixing = false;

        Room room = Hotel.getInstance().getRoomMap().get().get(roomId);
        if (room != null) {
            room.setStatus(RoomStatus.AVAILABLE);
            System.out.printf("Номер %d теперь доступен\n", roomId);
        }
    }

    @Override
    public String getPosition() {
        return "mender";
    }

    @Override
    public String toString() {
        return "Mender{" +
                "name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", patronymic='" + getPatronymic() + '\'' +
                '}';
    }

}
