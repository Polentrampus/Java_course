package hotel.personal.employee.service;

import hotel.model.Hotel;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;

public class Mender extends ServicePersonnelHotel{
    private boolean isFixing = false;
    public Mender(String name, String surname, String patronymic, int date_of_birth, int month_of_birth, int year_of_birth) {
        super(name, surname, patronymic, date_of_birth, month_of_birth, year_of_birth);
        System.out.println("Вы пригласили мастера" );
    }

    @Override
    public String toString() {
        return "Mender{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                '}';
    }

    public boolean isCleaning() {
        return isFixing;
    }

    @Override
    public void update(int roomId) {
        this.isFixing = true;
        System.out.printf("Мастер %s получил запрос на обслуживание номера %d\n",
                toString(), roomId);
        cleanRoom(roomId);
    }

    private void cleanRoom(int roomId) {
        System.out.printf("Мастер %s начал решать проблему номера %d\n",
                toString(), roomId);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.printf("Мастер %s закончил решать проблему номера %d\n",
                toString(), roomId);
        this.isFixing = false;

        Room room = Hotel.getInstance().getRoomMap().get(roomId);
        if (room != null) {
            room.setStatus(RoomStatus.AVAILABLE);
            System.out.printf("Номер %d теперь доступен\n", roomId);
        }
    }
}
