package hotel.personal.employee.service;

import hotel.model.Hotel;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;

public class Maid extends ServicePersonnelHotel{
    private boolean isCleaning = false;

    public Maid(String name, String surname, String patronymic, int date_of_birth, int month_of_birth, int year_of_birth) {
        super(name, surname, patronymic, date_of_birth, month_of_birth, year_of_birth);
        System.out.println("Вы пригласили горничную" );
    }

    @Override
    public String toString() {
        return "Maid{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
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
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
