package hotel.view.action;

import hotel.controller.AdminController;
import hotel.model.Hotel;
import hotel.model.filter.ClientFilter;
import hotel.model.service.Services;
import hotel.users.client.Client;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CheckClienOfHotel extends BaseAction {
    AdminController adminController;

    public CheckClienOfHotel(AdminController admin, Scanner scanner) {
        super(scanner);
        this.adminController = admin;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ЗАСЕЛЕНИЕ КЛИЕНТА ===");
            Collection<Client> clientCollection = adminController.getInfoAboutClientDatabase(ClientFilter.ID);

            List<Integer> availableClientIds = clientCollection.stream()
                    .map(Client::getId)
                    .collect(Collectors.toList());

            int clientId = readInt("Введите ID клиента: ");
            String clientName = readString("Введите имя клиента: ");
            String clientSurname = readString("Введите фамилию клиента: ");
            String clientPatronymic = readString("Введите отчество клиента: ");
            LocalDate clientBirthDay = readDate("Введите день рождения клиента: ");

            if (availableClientIds.contains(clientId)) {
                System.out.println("Клиент с таким ID уже существует!");
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
            int clientNumRoom = readInt("Введите номер комнаты клиента: ");
            LocalDate clientCheckoutDate = readDate("Введите дату заезда клиента: ");
            LocalDate clientDepartureDate = readDate("Введите дату выезда клиента: ");

            Client client = new Client(clientId, clientName, clientSurname,
                    clientPatronymic, clientBirthDay, services,
                    clientNumRoom, clientCheckoutDate, clientDepartureDate
                    );
            adminController.settle(client);

            System.out.println("Клиент заселен!");

        } catch (Exception e) {
            System.out.println("Ошибка при добавлении клиента: " + e.getMessage());
        }
    }
}
