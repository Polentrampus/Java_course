package hotel.exception.room;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class RoomNotAvailableException extends HotelException {
    public RoomNotAvailableException(Integer roomId, String reason) {
        super(ErrorCode.ROOM_NOT_AVAILABLE,
                "Комната " + roomId + " недоступна: " + reason);
        addDetail("roomId", roomId);
        addDetail("reason", reason);
    }
}