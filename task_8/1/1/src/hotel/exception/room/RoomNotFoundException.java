package hotel.exception.room;

public class RoomNotFoundException extends RoomException {
    public RoomNotFoundException(int roomId) {
        super("Комната с ID " + roomId + " не найдена");
    }
}
