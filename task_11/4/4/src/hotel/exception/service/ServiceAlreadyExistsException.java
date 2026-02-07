package hotel.exception.service;

import hotel.exception.ErrorCode;

public class ServiceAlreadyExistsException extends ServiceException {
    public ServiceAlreadyExistsException(ErrorCode code, String name) {
        super(code, "Услуга " + name + " уже существует");
    }
}
