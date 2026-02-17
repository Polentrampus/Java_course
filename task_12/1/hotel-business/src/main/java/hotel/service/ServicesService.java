package hotel.service;

import hotel.annotation.Component;
import hotel.model.service.Services;
import hotel.repository.service.ServicesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class ServicesService implements ServicesRepository {
    private ServicesRepository servicesRepository;
    private static final Logger log = LoggerFactory.getLogger(ServicesService.class);

    public ServicesService() {
    }

    public void setHotelRepository(ServicesRepository servicesRepository) {
        this.servicesRepository = servicesRepository;
    }

    @Override
    public Optional<Services> findById(int id) throws SQLException {
        log.info("findById()");
        Optional<Services> service = servicesRepository.findById(id);
        log.info("findById(): service: " + service);
        return service;
    }

    public List<Services> findAll() {
        log.info("findAll()");
        List<Services> services = servicesRepository.findAll();
        log.info("findAll(): services size: " + services.size());
        return services;
    }

    @Override
    public boolean save(Services services) {
        log.info("save()");
        boolean result = servicesRepository.save(services);
        log.info("save(): result: " + result);
        return result;
    }

    @Override
    public boolean update(Services services) {
        log.info("update()");
        boolean result = servicesRepository.update(services);
        log.info("update(): result: " + result);
        return result;
    }

    @Override
    public boolean delete(int id) {
        log.info("delete()");
        boolean result = servicesRepository.delete(id);
        log.info("delete(): result: " + result);
        return result;
    }

    public void addService(Services service) {
        log.info("addService()");
        servicesRepository.save(service);
        log.info("addService(): added service: " + service);
    }

    public void setPrice(String name, BigDecimal price) {
        log.info("setPrice()");
        if (servicesRepository.findByName(name).isEmpty()) {
            log.warn("setPrice(): service not found: " + name);
            System.out.println("Такой услуги не существует!");
            return;
        }
        Services services = servicesRepository.findByName(name).get();
        services.setPrice(price);
        log.info("setPrice(): set price for service " + name + " to " + price);
    }

    @Override
    public Optional<Services> findByName(String name) {
        log.info("findByName()");
        Optional<Services> service = servicesRepository.findByName(name);
        log.info("findByName(): service: " + service);
        return service;
    }
}