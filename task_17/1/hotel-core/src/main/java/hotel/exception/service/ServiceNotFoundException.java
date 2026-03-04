package hotel.exception.service;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

public class ServiceNotFoundException extends HotelException {
    public ServiceNotFoundException(Integer serviceId) {
        super(ErrorCode.SERVICE_NOT_FOUND,
                "Услуга с ID " + serviceId + " не найдена");
        addDetail("serviceId", serviceId);
    }

    public ServiceNotFoundException(String serviceName) {
        super(ErrorCode.SERVICE_NOT_FOUND,
                "Услуга с названием '" + serviceName + "' не найдена");
        addDetail("serviceName", serviceName);
    }
}