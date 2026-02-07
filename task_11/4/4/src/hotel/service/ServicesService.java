package hotel.service;

import hotel.annotation.Component;
import hotel.dto.CreateServiceRequest;
import hotel.exception.service.ServiceAlreadyExistsException;
import hotel.model.service.Services;
import hotel.repository.HotelRepository;
import hotel.repository.service.ServicesRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

@Component
public class ServicesService implements ServicesRepository{
    private ServicesRepository servicesRepository;

    public ServicesService() {
    }

    public void setHotelRepository(ServicesRepository servicesRepository) {
        this.servicesRepository = servicesRepository;
    }

    @Override
    public Optional<Services> findById(int id) throws SQLException {
        return servicesRepository.findById(id);
    }

    public List<Services> findAll() {
        return servicesRepository.findAll();
    }

    @Override
    public boolean save(Services services) {
        return servicesRepository.save(services);
    }

    @Override
    public boolean update(Services services) {
        return servicesRepository.update(services);
    }

    @Override
    public boolean delete(int id) {
        return servicesRepository.delete(id);
    }

    public void addService(Services service) {
        servicesRepository.save(service);
    }

    public void setPrice(String name, BigDecimal price) {
        if(servicesRepository.findByName(name).isEmpty()){
            System.out.println("Такой услуги не существует!");
            return;
        }
        Services services = servicesRepository.findByName(name).get();
        services.setPrice(price);
    }

    @Override
    public Optional<Services> findByName(String name) {
        return servicesRepository.findByName(name);
    }
}
