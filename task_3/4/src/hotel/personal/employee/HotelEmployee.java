package hotel.personal.employee;

import hotel.model.Hotel;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.personal.Person;
import hotel.personal.client.Client;
import hotel.personal.employee.admin.Administration;
import hotel.personal.employee.service.Maid;
import hotel.personal.employee.service.Mender;

import java.util.List;

public abstract class HotelEmployee extends Person implements Administration {
    protected final Hotel hotel = Hotel.getInstance();

    public HotelEmployee(String name, String surname, String patronymic,
                         int date_of_birth, int month_of_birth, int year_of_birth) {
        super(name, surname, patronymic, date_of_birth, month_of_birth, year_of_birth);
    }

    @Override
    public void addClient(Client person) {
        hotel.getClientMap().put(person.getId(), person);
        System.out.println(getPosition() + " добавил клиента: " + person);
    }

    @Override
    public void cleaningRequest(int roomId) {
        Room room = hotel.getRoomMap().get(roomId);
        if (room != null) {
            room.setStatus(RoomStatus.CLEANING);
            System.out.printf("%s изменил статус комнаты %d на %s\n",
                    getPosition(), roomId, room.getStatus().getDescription());
            List<Maid> maids = hotel.getAvailableMaids();
            notifyMaids(maids, roomId);
        }
    }

    @Override
    public void repairRequest(int roomId) {
        Room room = hotel.getRoomMap().get(roomId);
        if (room != null) {
            room.setStatus(RoomStatus.MAINTENANCE);
            System.out.printf("%s изменил статус комнаты %d на %s\n",
                    getPosition(), roomId, room.getStatus().getDescription());
            List<Mender> mender = hotel.getAvailableMender();
            notifyMenders(mender, roomId);
        }
    }

    private void notifyMaids(List<Maid> maids, int roomId) {
        if (maids.isEmpty()) {
            System.out.println("Нет доступных горничных для уборки номера " + roomId);
            return;
        }

        Maid availableMaid = null;
        for(Maid maid : maids){
            if(!maid.isCleaning())
                availableMaid = maid;break;
        }

        if (availableMaid != null) {
            availableMaid.update(roomId);
        } else {
            System.out.println("Все горничные заняты, номер " + roomId + " будет убран позже");
        }
    }

    private void notifyMenders(List<Mender> menders, int roomId) {
        if (menders.isEmpty()) {
            System.out.println("Нет доступных мастеров для устранения проблемы в номере " + roomId);
            return;
        }

        Mender availableMender = null;
        for(Mender mender : menders){
            if(!mender.isCleaning())
                availableMender = mender;break;
        }

        if (availableMender != null) {
            availableMender.update(roomId);
        } else {
            System.out.println("Все мастера заняты, номер " + roomId + " будет отремонтирован позже");
        }
    }

    public abstract String getPosition();

    @Override
    public void settle(int idClient, int idRoom){
        if(hotel.getRoomMap().get(idRoom).getStatus() == RoomStatus.AVAILABLE){
            hotel.getRoomMap().get(idRoom).setStatus(RoomStatus.OCCUPIED);
            hotel.getClientMap().get(idClient).setIdRoom(idRoom);
            System.out.printf("%s поселил клиента %s", getPosition() ,hotel.getClientMap().get(idClient).toString() +
                    " в номер:" + hotel.getClientMap().get(idClient).getIdRoom() + "\n");
            return;
        }
        System.out.println("Не удалось заселить в выбранный номер " + idRoom +
                "\nДанный номер находится в статусе: " + hotel.getRoomMap().get(idRoom).getStatus().getDescription());
    }

    @Override
    public void evict(int idClient){
        Client client = hotel.getClientMap().get(idClient);
        if (client == null) {
            System.out.println("Клиент с ID " + idClient + " не найден");
            return;
        }

        int roomId = client.getIdRoom();
        if (roomId == 0) {
            System.out.println("У клиента " + client + " не указан номер");
            return;
        }

        Room room = hotel.getRoomMap().get(roomId);
        if (room == null) {
            System.out.println("Номер " + roomId + " не существует");
            return;
        }

        System.out.println("Клиент: " + client + " успешно выселился");
        client.setIdRoom(0);
        cleaningRequest(roomId);
    }
}