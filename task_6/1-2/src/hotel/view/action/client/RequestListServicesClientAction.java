package hotel.view.action.client;

import hotel.controller.AdminController;
import hotel.model.filter.ClientFilter;
import hotel.model.filter.ServicesFilter;
import hotel.users.client.Client;
import hotel.view.action.BaseAction;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class RequestListServicesClientAction extends BaseAction {
    private final AdminController adminController;

    public RequestListServicesClientAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== УСЛУГИ КЛИЕНТА ===");
            Collection<Client> clientCollection =  adminController.getInfoAboutClientDatabase(ClientFilter.ID);
            if(clientCollection.isEmpty()){
                System.out.println("В базе данных нет клиентов");
                return;
            }

            List<Integer> availableClientIds = clientCollection.stream()
                    .map(Client::getId)
                    .collect(Collectors.toList());

            int clientId = readInt("Введите ID клиента: ",
                    Collections.min(availableClientIds),
                    Collections.max(availableClientIds));

            if (!availableClientIds.contains(clientId)) {
                System.out.println("Клиента с таким ID не существует!");
                return;
            }
            ServicesFilter filter = readEnum(ServicesFilter.class, "Выберите фильтр услуг:");
            adminController.requestListServicesClient(filter, clientId);
        } catch (Exception e) {
            System.out.println("Ошибка при получении услуг клиента: " + e.getMessage());
        }
    }
}