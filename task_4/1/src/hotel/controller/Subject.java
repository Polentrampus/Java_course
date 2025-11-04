package hotel.controller;

import hotel.users.employee.service.Observer;

public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
}
