package hotel.exception.room;

import hotel.exception.ErrorCode;

public class RoomNotFoundException extends RoomException {
    public RoomNotFoundException(int roomId) {
        super(ErrorCode.ROOM_NOT_FOUND,
                "Комната с ID " + roomId + " не найдена");
        addDetail("roomId", roomId);
    }
}
