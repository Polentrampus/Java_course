package hotel.view.action.client;

import hotel.controller.AdminController;
import hotel.model.Hotel;
import hotel.model.filter.ClientFilter;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.view.action.BaseAction;

import java.util.*;
import java.util.stream.Collectors;

public class AddClientServicesAction extends BaseAction {
    private final AdminController adminController;

    public AddClientServicesAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ДОБАВЛЕНИЕ УСЛУГ КЛИЕНТУ ===");
            Collection<Client> clientCollection = adminController.getInfoAboutClientDatabase(ClientFilter.ID);
            if (clientCollection.isEmpty()) {
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
            System.out.println("\nДоступные услуги:");
            System.out.println(adminController.requestListServices());
            System.out.println("0) назад");
            List<Services> services = new ArrayList<>();
            while (true) {
                System.out.print("Выбор: ");
                String choice = scanner.nextLine().trim();
                if (choice.equals("0")) break;

                if (Hotel.getInstance().getService(choice).isEmpty()) {
                    System.out.println("Такой услуги не существует!");
                    continue;
                }
                services.add(Hotel.getInstance().getService(choice).get());
            }

            if (services.isEmpty()) {
                System.out.println("Вы ничего не добавили!");
                return;
            }

            adminController.addClientServices(clientId, services);
            System.out.println("Услуги добавлены клиенту!");

        } catch (Exception e) {
            System.out.println("Ошибка при добавлении услуг клиенту: " + e.getMessage());
        }
    }
}