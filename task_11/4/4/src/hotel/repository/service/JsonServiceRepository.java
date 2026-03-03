package hotel.repository.service;

import hotel.model.service.Services;
import hotel.util.JsonDataManager;

import java.io.IOException;
import java.util.*;

public class JsonServiceRepository implements ServicesRepository {
    private final JsonDataManager dataManager = JsonDataManager.getInstance();
    public JsonServiceRepository() {
    }

    @Override
    public Optional<Services> findById(int id) {
        return Optional.ofNullable(dataManager.getServices().get(id));
    }

    @Override
    public List<Services> findAll() {
        return new ArrayList<>(dataManager.getServices().values());
    }

    @Override
    public boolean save(Services service) {
        if (service.getId() == null) {
            int maxId = dataManager.getServices().keySet().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);
            service.setId(maxId + 1);
        }

        dataManager.saveService(service);
        return true;    }

    @Override
    public boolean update(Services service) {
        dataManager.getServices().put(service.getId(), service);
        return true;
    }

    @Override
    public boolean delete(int id) {
        dataManager.deleteService(id);
        return true;
    }

    @Override
    public Optional<Services> findByName(String name) {
        return dataManager.getServices().values().stream()
                .filter(service -> service.getName().equals(name))
                .findFirst();
    }
}