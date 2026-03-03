package hotel.view.action.client;

import hotel.model.users.client.Client;
import hotel.service.ClientService;
import hotel.view.action.BaseAction;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class CheckClientOfHotel extends BaseAction {
    private final ClientService clientService;

    public CheckClientOfHotel(ClientService clientService, Scanner scanner) {
        super(scanner);
        this.clientService = clientService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ДОБАВЛЕНИЕ КЛИЕНТА ===");

            Collection<Client> existingClients = clientService.findAll();
            List<Integer> existingClientIds = existingClients.stream()
                    .map(Client::getId)
                    .toList();

            System.out.println("\nВведите данные нового клиента:");
            Client client = new Client();
            client.setName(readString("Имя: "));
            client.setSurname(readString("Фамилия: "));
            client.setPatronymic(readString("Отчество: "));
            client.setDateOfBirth(readDate("Дата рождения"));
            client.setNotes(readString("Дополнительно: "));

            Integer isAdded = clientService.save(client);

            if (isAdded != 0) {
                System.out.println("\nКлиент успешно добавлен!");
                System.out.println(client.toString());
            } else {
                System.out.println("\n✗ Не удалось заселить клиента.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении клиента: " + e.getMessage());
        }
    }
}