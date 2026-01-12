package hotel.exception.service;

import hotel.exception.room.RoomException;

public class ServiceAlreadyExistsException extends RoomException {
    public ServiceAlreadyExistsException(String name) {
        super("Услуга " + name + " уже существует");
    }
}
