package hotel.view.action.client;


import hotel.service.ClientService;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class RequestLastThreeClientAction extends BaseAction {
    private final ClientService clientService;

    public RequestLastThreeClientAction(ClientService clientService, Scanner scanner) {
        super(scanner);
        this.clientService = clientService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ПОСЛЕДНИЕ 3 КЛИЕНТА ===");
            clientService.requestLastThreeClient();
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка клиентов: " + e.getMessage());
        }
    }
}