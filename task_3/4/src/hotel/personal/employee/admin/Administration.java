package hotel.personal.employee.admin;

import hotel.personal.client.Client;

public interface Administration {
    void settle(int idClient, int idRoom);
    void evict(int idClient);
    void cleaningRequest(int idRoom);
    void repairRequest(int idRoom);
    void addClient(Client person);
}
