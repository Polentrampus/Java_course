package hotel.view.action.client;

import hotel.model.users.client.Client;
import hotel.service.ClientService;
import hotel.view.action.BaseAction;

import java.util.List;
import java.util.Scanner;

public class GetInfoAboutClientAction extends BaseAction {
    private final ClientService clientService;

    public GetInfoAboutClientAction(ClientService clientService, Scanner scanner) {
        super(scanner);
        this.clientService = clientService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ИНФОРМАЦИЯ О КЛИЕНТЕ ===");

            List<Client> clientCollection = clientService.findAll();
            if (clientCollection.isEmpty()) {
                System.out.println("В базе данных нет клиентов.");
                return;
            }

            System.out.println("\nСписок клиентов:");
            clientCollection.forEach(client ->
                    System.out.println("ID: " + client.getId() +
                            " | " + client.getSurname() + " " +
                            client.getName() + " " + client.getPatronymic()));

            int clientId = readInt("\nВведите ID клиента: ");

            if (clientService.findById(clientId).isEmpty()) {
                System.out.println("Клиент с ID " + clientId + " не найден.");
                return;
            }
            Client client = clientService.findById(clientId).get();

            System.out.println("ПОДРОБНАЯ ИНФОРМАЦИЯ О КЛИЕНТЕ");
            System.out.println("ID: " + client.getId());
            System.out.println("Фамилия: " + client.getSurname());
            System.out.println("Имя: " + client.getName());
            System.out.println("Отчество: " + client.getPatronymic());
            System.out.println("Дата рождения: " + client.getDateOfBirth());
            System.out.println("Возраст: " + calculateAge(client.getDateOfBirth()) + " лет");
            System.out.println("Дополнительно: " + (client.getNotes()));
        } catch (Exception e) {
            System.out.println("Ошибка при получении информации о клиенте: " + e.getMessage());
        }
    }

    private int calculateAge(java.time.LocalDate birthDate) {
        return java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears();
    }
}