package hotel.exception.room;

public class RoomAlreadyExistsException extends RoomException {
    public RoomAlreadyExistsException(int roomNumber) {
        super("Комната с номером " + roomNumber + " уже существует");
    }
}
