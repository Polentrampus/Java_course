package hotel.exception.room;

import hotel.exception.ErrorCode;

public class RoomAlreadyExistsException extends RoomException {
    public RoomAlreadyExistsException(ErrorCode code, int roomNumber) {
        super(code, "Комната с номером " + roomNumber + " уже существует");
    }
}
