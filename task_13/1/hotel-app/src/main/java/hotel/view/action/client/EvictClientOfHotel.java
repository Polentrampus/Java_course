package hotel.view.action.client;

import hotel.model.users.client.Client;
import hotel.service.ClientService;
import hotel.view.action.BaseAction;

import java.util.List;
import java.util.Scanner;

public class EvictClientOfHotel extends BaseAction {
    private final ClientService clientService;

    public EvictClientOfHotel(ClientService clientService, Scanner scanner) {
        super(scanner);
        this.clientService = clientService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== УДАЛЕНИЕ КЛИЕНТА ИЗ СИСТЕМЫ ===");

            List<Client> clients = clientService.findAll();

            if (clients.isEmpty()) {
                System.out.println("В базе данных нет клиентов.");
                return;
            }

            System.out.println("\nСписок клиентов:");
            clients.forEach(client ->
                    System.out.println("ID: " + client.getId() +
                            " | " + client.getSurname() + " " +
                            client.getName() + " " + client.getPatronymic()));

            int clientId = readInt("\nВведите ID клиента для выселения: ");

            if (clientService.findById(clientId).isEmpty()) {
                System.out.println("Клиент с ID " + clientId + " не найден.");
                return;
            }
            Client clientToEvict = clientService.findById(clientId).get();

            System.out.println("\nИнформация о клиенте:");
            System.out.println("ФИО: " + clientToEvict.getSurname() + " " +
                    clientToEvict.getName() + " " + clientToEvict.getPatronymic());
            System.out.println("Дата рождения: " + clientToEvict.getDateOfBirth());
            System.out.println("Дополнительно:: " + clientToEvict.getNotes());

            String confirm = readString("\nВы уверены, что хотите выселить этого клиента? (yes/no): ");
            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("Выселение отменено.");
                return;
            }
            clientService.delete(clientId);
            System.out.println("Клиент удален!");
        } catch (Exception e) {
            System.out.println("Ошибка при удалении клиента: " + e.getMessage());
        }
    }
}