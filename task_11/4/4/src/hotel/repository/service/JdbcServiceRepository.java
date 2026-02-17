package hotel.repository.service;


import hotel.dao.ServiceDAO;
import hotel.model.service.Services;

import java.util.List;
import java.util.Optional;

public class JdbcServiceRepository implements ServicesRepository {
    private final ServiceDAO serviceDao = ServiceDAO.getInstance();

    @Override
    public Optional<Services> findById(int id) {
        try {
            return serviceDao.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find service by id", e);
        }
    }

    @Override
    public List<Services> findAll() {
        try {
            return serviceDao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all services", e);
        }
    }

    @Override
    public boolean save(Services service) {
        try {
            serviceDao.save(service);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save service", e);
        }
    }

    @Override
    public boolean update(Services service) {
        try {
            serviceDao.update(service);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update service", e);
        }
    }

    @Override
    public boolean delete(int id) {
        try {
            serviceDao.delete(id);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete service", e);
        }
    }

    @Override
    public Optional<Services> findByName(String name) {
        try {
            return serviceDao.findByName(name);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find service by name", e);
        }
    }
}