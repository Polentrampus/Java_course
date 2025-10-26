package hotel.personal.employee.service;

import hotel.model.Hotel;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.personal.employee.Employee;

import java.time.LocalDate;

public class Maid extends Employee implements Observer{
    private boolean isCleaning = false;

    public Maid(int id, String name, String surname, String patronymic, LocalDate date_of_birth) {
        super(id, name, surname, patronymic, date_of_birth);
        System.out.println("Вы пригласили горничную" );
    }

    @Override
    public String getPosition() {
        return "maid";
    }

    @Override
    public String toString() {
        return "Maid{" +
                "name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", patronymic='" + getPatronymic() + '\'' +
                '}';
    }

    public boolean isCleaning() {
        return isCleaning;
    }

    @Override
    public void update(int roomId) {
        this.isCleaning = true;
        System.out.printf("Горничная %s получила запрос на уборку номера %d\n",
                toString(), roomId);
        cleanRoom(roomId);
    }

    private void cleanRoom(int roomId) {
        System.out.printf("Горничная %s начала уборку номера %d\n",
                toString(), roomId);
        System.out.printf("Горничная %s закончила уборку номера %d\n",
                toString(), roomId);
        this.isCleaning = false;

        // Обновляем статус комнаты
        Room room = Hotel.getInstance().getRoomMap().get(roomId);
        if (room != null) {
            room.setStatus(RoomStatus.AVAILABLE);
            System.out.printf("Номер %d теперь доступен\n", roomId);
        }
    }
}
