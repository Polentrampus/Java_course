package hotel.service;

import hotel.dto.CreateServiceRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.dao.DAOException;
import hotel.exception.service.ServiceAlreadyExistsException;
import hotel.exception.service.ServiceNotFoundException;
import hotel.model.service.Services;
import hotel.repository.service.ServicesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service("servicesService")
@Transactional
public class ServicesService {
    private final ServicesRepository servicesRepository;
    private static final Logger log = LoggerFactory.getLogger(ServicesService.class);

    @Autowired
    public ServicesService(ServicesRepository servicesRepository) {
        this.servicesRepository = servicesRepository;
    }

    public Optional<Services> findById(Integer id) {
        log.info("Finding service by id: {}", id);

        if (id == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID услуги не может быть null");
        }

        try {
            Optional<Services> service = servicesRepository.findById(id);
            log.info("Service found: {}", service.isPresent());
            return service;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while finding service by id: {}", id, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при поиске услуги по ID", e);
        }
    }

    public List<Services> findAll() {
        log.info("Finding all services");

        try {
            List<Services> services = servicesRepository.findAll();
            log.info("Found {} services", services.size());
            return services;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while finding all services", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при получении списка всех услуг", e);
        }
    }

    public Services save(Services services) {
        log.info("Saving service: {}", services.getName());

        validateService(services);

        try {
            if (servicesRepository.findByName(services.getName()).isPresent()) {
                throw new ServiceAlreadyExistsException(ErrorCode.SERVICE_ALREADY_EXISTS, services.getName());
            }

            Integer savedId = servicesRepository.save(services);
            log.info("Service saved with id: {}", savedId);

            return servicesRepository.findById(savedId)
                    .orElseThrow(() -> new HotelException(ErrorCode.DATA_INTEGRITY_VIOLATION,
                            "Не удалось найти сохраненную услугу"));

        } catch (ServiceAlreadyExistsException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while saving service: {}", services.getName(), e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при сохранении услуги", e);
        } catch (Exception e) {
            log.error("Unexpected error while saving service: {}", services.getName(), e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при сохранении услуги", e);
        }
    }

    public void update(Services services) {
        log.info("Updating service with id: {}", services != null ? services.getId() : null);

        if (services == null || services.getId() == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Услуга и её ID не могут быть null");
        }

        validateService(services);

        try {
            if (!servicesRepository.findById(services.getId()).isPresent()) {
                throw new ServiceNotFoundException(services.getId());
            }

            servicesRepository.update(services);
            log.info("Service updated successfully with id: {}", services.getId());

        } catch (ServiceNotFoundException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while updating service with id: {}", services.getId(), e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при обновлении услуги", e);
        } catch (Exception e) {
            log.error("Unexpected error while updating service with id: {}", services.getId(), e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при обновлении услуги", e);
        }
    }

    public void delete(Integer id) {
        log.info("Deleting service with id: {}", id);

        if (id == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID услуги не может быть null");
        }

        try {
            Services services = servicesRepository.findById(id)
                    .orElseThrow(() -> new ServiceNotFoundException(id));

            servicesRepository.delete(services);
            log.info("Service deleted successfully with id: {}", id);

        } catch (ServiceNotFoundException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while deleting service with id: {}", id, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при удалении услуги", e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting service with id: {}", id, e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при удалении услуги", e);
        }
    }

    public Optional<Services> addService(CreateServiceRequest request) {
        log.info("Adding new service: {}", request.getName());

        validateCreateServiceRequest(request);

        try {
            if (servicesRepository.findByName(request.getName()).isPresent()) {
                throw new ServiceAlreadyExistsException(ErrorCode.SERVICE_ALREADY_EXISTS, request.getName());
            }

            Services service = new Services();
            service.setName(request.getName());
            service.setPrice(request.getPrice());
            service.setDescription(request.getDescription());

            Integer serviceId = servicesRepository.save(service);
            log.info("Service added with id: {}", serviceId);

            return Optional.ofNullable(servicesRepository.findById(serviceId)
                    .orElseThrow(() -> new HotelException(ErrorCode.DATA_INTEGRITY_VIOLATION,
                            "Не удалось найти добавленную услугу")));

        } catch (ServiceAlreadyExistsException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while adding service: {}", request.getName(), e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при добавлении услуги", e);
        } catch (Exception e) {
            log.error("Unexpected error while adding service: {}", request.getName(), e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при добавлении услуги", e);
        }
    }

    public void setPrice(Integer serviceId, BigDecimal price) {
        log.info("Setting price for service {} to {}", serviceId, price);

        if (serviceId == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID услуги не может быть null");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new HotelException(ErrorCode.SERVICE_INVALID_PRICE, price.toString());
        }

        try {
            Services services = servicesRepository.findById(serviceId)
                    .orElseThrow(() -> new ServiceNotFoundException(serviceId));

            services.setPrice(price);
            servicesRepository.update(services);

            log.info("Price for service {} set to {}", serviceId, price);

        } catch (HotelException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while setting price for service: {}", serviceId, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при изменении цены услуги", e);
        } catch (Exception e) {
            log.error("Unexpected error while setting price for service: {}", serviceId, e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при изменении цены услуги", e);
        }
    }

    public void setPrice(String name, BigDecimal price) {
        log.info("Setting price for service {} to {}", name, price);

        if (name == null || name.trim().isEmpty()) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Название услуги не может быть пустым");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new HotelException(ErrorCode.SERVICE_INVALID_PRICE, price.toString());
        }

        try {
            Services services = servicesRepository.findByName(name)
                    .orElseThrow(() -> new ServiceNotFoundException(name));

            services.setPrice(price);
            servicesRepository.update(services);

            log.info("Price for service {} set to {}", name, price);

        } catch (HotelException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while setting price for service: {}", name, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при изменении цены услуги", e);
        } catch (Exception e) {
            log.error("Unexpected error while setting price for service: {}", name, e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при изменении цены услуги", e);
        }
    }

    public Optional<Services> findByName(String name) {
        log.info("Finding service by name: {}", name);

        if (name == null || name.trim().isEmpty()) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Название услуги не может быть пустым");
        }

        try {
            Optional<Services> service = servicesRepository.findByName(name);
            log.info("Service found by name: {}", service.isPresent());
            return service;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while finding service by name: {}", name, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при поиске услуги по названию", e);
        }
    }

    private void validateService(Services services) {
        if (services == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Услуга не может быть null");
        }
        if (services.getName() == null || services.getName().trim().isEmpty()) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Название услуги обязательно");
        }
        if (services.getName().length() < 2 || services.getName().length() > 100) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR,
                    "Название услуги должно быть от 2 до 100 символов");
        }
        if (services.getPrice() == null || services.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new HotelException(ErrorCode.SERVICE_INVALID_PRICE, services.getPrice().toString());
        }
    }

    private void validateCreateServiceRequest(CreateServiceRequest request) {
        if (request == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Запрос на создание услуги не может быть null");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Название услуги обязательно");
        }
        if (request.getName().length() < 2 || request.getName().length() > 100) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR,
                    "Название услуги должно быть от 2 до 100 символов");
        }
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new HotelException(ErrorCode.SERVICE_INVALID_PRICE, request.getPrice().toString());
        }
    }
}