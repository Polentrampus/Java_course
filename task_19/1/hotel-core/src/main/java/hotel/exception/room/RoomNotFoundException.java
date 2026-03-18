package hotel.exception.room;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class RoomNotFoundException extends HotelException {
    public RoomNotFoundException(Integer roomId) {
        super(ErrorCode.ROOM_NOT_FOUND,
                "Комната с номером " + roomId + " не найдена");
        addDetail("roomId", roomId);
    }
}