package hotel.view.action.client;

import hotel.controller.AdminController;
import hotel.model.filter.ClientFilter;
import hotel.model.users.client.Client;
import hotel.view.action.BaseAction;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class EvictClientOfHotel extends BaseAction {
    AdminController adminController;

    public EvictClientOfHotel(AdminController admin, Scanner scanner) {
        super(scanner);
        this.adminController = admin;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ВЫСЕЛЕНИЕ КЛИЕНТА ===");
            Collection<Client> clientCollection = adminController.getInfoAboutClientDatabase(ClientFilter.ID);

            List<Integer> availableClientIds = clientCollection.stream()
                    .map(Client::getId)
                    .collect(Collectors.toList());

            int clientId = readInt("Введите ID клиента: ");

            if (!availableClientIds.contains(clientId)) {
                System.out.println("Клиент с таким ID не существует!");
                return;
            }

            adminController.evict(clientId);
            System.out.println("Клиент выселен!");

        } catch (Exception e) {
            System.out.println("Ошибка при выселении клиента: " + e.getMessage());
        }

    }
}
