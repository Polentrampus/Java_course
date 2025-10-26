package hotel.model.controller;

import hotel.personal.employee.service.Observer;

public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
}
