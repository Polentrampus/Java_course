package hotel.view.action.client;


import hotel.model.filter.ClientFilter;
import hotel.model.users.client.Client;
import hotel.service.ClientService;
import hotel.view.action.BaseAction;

import java.util.List;
import java.util.Scanner;

public class GetInfoAboutClientDatabaseAction extends BaseAction {
    private final ClientService clientService;

    public GetInfoAboutClientDatabaseAction(ClientService clientService, Scanner scanner) {
        super(scanner);
        this.clientService = clientService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== БАЗА ДАННЫХ КЛИЕНТОВ ===");
            ClientFilter filter = readEnum(ClientFilter.class, "Выберите фильтр клиентов:");
            List<Client> clients = clientService.getInfoAboutClientDatabase(filter);
            for (Client client : clients) {
                System.out.println(client);
            }
            System.out.println("ВСего клиентов: " + clients.size());
        } catch (Exception e) {
            System.out.println("Ошибка при получении информации о клиентах: " + e.getMessage());
        }
    }
}