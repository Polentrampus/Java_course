package hotel.service;

import hotel.annotation.Component;
import hotel.annotation.Inject;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.model.service.Services;
import hotel.repository.service.ServicesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class ServicesService {

    @Inject
    private ServicesRepository servicesRepository;

    @Inject
    private TransactionManager transactionManager;

    private static final Logger log = LoggerFactory.getLogger(ServicesService.class);

    public ServicesService() {
    }

    public Optional<Services> findById(Integer id) {
        log.info("findById() for id: {}", id);

        return transactionManager.executeInTransaction(() -> {
            Optional<Services> service = servicesRepository.findById(id);
            log.info("findById(): service found: {}", service.isPresent());
            return service;
        });
    }

    public List<Services> findAll() {
        log.info("findAll()");

        return transactionManager.executeInTransaction(() -> {
            List<Services> services = servicesRepository.findAll();
            log.info("findAll(): found {} services", services.size());
            return services;
        });
    }

    public Integer save(Services services) {
        log.info("save() for service: {}", services);

        return transactionManager.executeInTransaction(() -> {
            if (servicesRepository.findByName(services.getName()).isPresent()) {
                throw new HotelException(ErrorCode.SERVICE_ALREADY_EXISTS,
                        "Услуга с названием '" + services.getName() + "' уже существует");
            }

            Integer result = servicesRepository.save(services);
            log.info("save(): service saved with id: {}", result);
            return result;
        });
    }

    public boolean update(Services services) {
        log.info("update() for service: {}", services);

        return transactionManager.executeInTransaction(() -> {
            if (!servicesRepository.findById(services.getId()).isPresent()) {
                throw new HotelException(ErrorCode.SERVICE_NOT_FOUND,
                        "Услуга не найдена для обновления");
            }

            servicesRepository.update(services);
            log.info("update()");
            return null;
        });
    }

    public boolean delete(Integer id) {
        log.info("delete() for id: {}", id);

        return transactionManager.executeInTransaction(() -> {
            Optional<Services> services = servicesRepository.findById(id);
            servicesRepository.delete(services.get());
            log.info("delete()");
            return null;
        });
    }

    public void addService(Services service) {
        log.info("addService() for service: {}", service);

        transactionManager.executeInTransaction(() -> {
            servicesRepository.save(service);
            log.info("addService(): added service: {}", service);
            return null;
        });
    }

    public void setPrice(String name, BigDecimal price) {
        log.info("setPrice() for service: {}, new price: {}", name, price);

        transactionManager.executeInTransaction(() -> {
            Services services = servicesRepository.findByName(name)
                    .orElseThrow(() -> new HotelException(ErrorCode.SERVICE_NOT_FOUND,
                            "Услуга с названием '" + name + "' не найдена"));

            services.setPrice(price);
            servicesRepository.update(services);

            System.out.println("Цена услуги '" + name + "' изменена на " + price);
            log.info("setPrice(): set price for service {} to {}", name, price);
            return null;
        });
    }

    public Optional<Services> findByName(String name) {
        log.info("findByName() for name: {}", name);

        return transactionManager.executeInTransaction(() -> {
            Optional<Services> service = servicesRepository.findByName(name);
            log.info("findByName(): service found: {}", service.isPresent());
            return service;
        });
    }
}