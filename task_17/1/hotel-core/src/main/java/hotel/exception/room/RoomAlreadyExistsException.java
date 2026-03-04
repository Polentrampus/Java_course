package hotel.exception.room;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class RoomAlreadyExistsException extends HotelException {
    public RoomAlreadyExistsException(Integer roomNumber) {
        super(ErrorCode.ROOM_ALREADY_EXISTS,
                "Комната с номером " + roomNumber + " уже существует");
        addDetail("roomNumber", roomNumber);
    }
}