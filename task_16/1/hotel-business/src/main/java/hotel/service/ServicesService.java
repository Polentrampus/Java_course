package hotel.service;

import hotel.dto.CreateServiceRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.model.service.Services;
import hotel.repository.service.ServicesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;
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

    public Optional<Services> findById(Integer id) throws SQLException {
        log.info("findById() for id: {}", id);

        Optional<Services> service = servicesRepository.findById(id);
        log.info("findById(): service found: {}", service.isPresent());
        return service;
    }

    public List<Services> findAll() {
        log.info("findAll()");

        List<Services> services = servicesRepository.findAll();
        log.info("findAll(): found {} services", services.size());
        return services;
    }

    public Integer save(Services services) {
        log.info("save() for service: {}", services);

        if (servicesRepository.findByName(services.getName()).isPresent()) {
            throw new HotelException(ErrorCode.SERVICE_ALREADY_EXISTS,
                    "Услуга с названием '" + services.getName() + "' уже существует");
        }

        Integer result = servicesRepository.save(services);
        log.info("save(): service saved with id: {}", result);
        return result;
    }

    public boolean update(Services services) throws SQLException {
        log.info("update() for service: {}", services);

        if (!servicesRepository.findById(services.getId()).isPresent()) {
            throw new HotelException(ErrorCode.SERVICE_NOT_FOUND,
                    "Услуга не найдена для обновления");
        }

        servicesRepository.update(services);
        log.info("update()");
        return true;
    }

    public boolean delete(Integer id) throws SQLException {
        log.info("delete() for id: {}", id);

        Optional<Services> services = servicesRepository.findById(id);
        servicesRepository.delete(services.get());
        log.info("delete()");
        return true;
    }

    public Optional<Services> addService(CreateServiceRequest request) throws SQLException {
        log.info("addService() for service: {}", request.getName());
        Services service = new Services();
        service.setName(request.getName());
        service.setPrice(BigDecimal.valueOf(request.getPrice()));
        service.setDescription(request.getDescription());
        Integer serviceId = servicesRepository.save(service);
        log.info("addService(): added service: {}", service);
        return findById(serviceId);
    }

    public void setPrice(String name, BigDecimal price) throws SQLException {
        log.info("setPrice() for service: {}, new price: {}", name, price);

        Services services = servicesRepository.findByName(name)
                .orElseThrow(() -> new HotelException(ErrorCode.SERVICE_NOT_FOUND,
                        "Услуга с названием '" + name + "' не найдена"));

        services.setPrice(price);
        servicesRepository.update(services);

        System.out.println("Цена услуги '" + name + "' изменена на " + price);
        log.info("setPrice(): set price for service {} to {}", name, price);
    }

    public Optional<Services> findByName(String name) {
        log.info("findByName() for name: {}", name);

        Optional<Services> service = servicesRepository.findByName(name);
        log.info("findByName(): service found: {}", service.isPresent());
        return service;
    }
}