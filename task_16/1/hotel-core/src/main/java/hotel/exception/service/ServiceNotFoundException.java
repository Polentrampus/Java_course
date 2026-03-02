package hotel.exception.service;

import hotel.exception.ErrorCode;
import hotel.exception.client.ClientException;

public class ServiceNotFoundException extends ClientException {
    public ServiceNotFoundException(int serviceID) {
        super(ErrorCode.SERVICE_NOT_FOUND,
                "Услуга с ID " + serviceID + " не найден");
        addDetail("serviceID", serviceID);
    }
}
