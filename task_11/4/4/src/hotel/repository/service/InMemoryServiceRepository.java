package hotel.repository.service;


import hotel.model.service.Services;

import java.math.BigDecimal;
import java.util.*;

public class InMemoryServiceRepository implements ServicesRepository {
    private final Map<Integer, Services> serviceMap = new HashMap<>();

    public InMemoryServiceRepository() {
        initializeTestData();
    }

    private void initializeTestData() {
        save(new Services(1, "BAGGAGE_STORAGE", "Услуга хранения вашего багажа", BigDecimal.valueOf(100.00)));
        save(new Services(2, "FOOD_DELIVERY", "Доставка еды в номер",  BigDecimal.valueOf(1200.0)));
        save(new Services(3, "TRANSFER", "Трансфер из/в аэропорт",  BigDecimal.valueOf(2500.0)));
        save(new Services(4, "FITNESS_CENTER", "Доступ в фитнес-центр",  BigDecimal.valueOf(800.0)));
        save(new Services(5, "MINI_BAR", "В стоимость номера будет включен мини бар", BigDecimal.valueOf(100.3)));
        save(new Services(6, "SPA", "Возможность посетить спа", BigDecimal.valueOf(0.0)));
        save(new Services(7, "CONCIERGE", "Консьерж-сервис", BigDecimal.valueOf(10.0)));
    }

    @Override
    public Optional<Services> findById(int id) {
        return Optional.ofNullable(serviceMap.get(id));
    }

    @Override
    public List<Services> findAll() {
        return List.copyOf(serviceMap.values());
    }

    @Override
    public boolean save(Services service) {
        serviceMap.put(service.getId(), service);
        return true;
    }

    @Override
    public boolean update(Services service) {
        if (serviceMap.containsKey(service.getId())) {
            serviceMap.put(service.getId(), service);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        return serviceMap.remove(id) != null;
    }

    @Override
    public Optional<Services> findByName(String name) {
        return serviceMap.values().stream()
                .filter(service -> service.getName().equals(name))
                .findFirst();
    }
}