package hotel.view.action.services;

import hotel.service.ServicesService;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class RequestListServicesAction extends BaseAction {
    private final ServicesService servicesService;

    public RequestListServicesAction(ServicesService servicesService, Scanner scanner) {
        super(scanner);
        this.servicesService = servicesService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== СПИСОК УСЛУГ ===");
            servicesService.findAll().forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка услуг: " + e.getMessage());
        }
    }
}
