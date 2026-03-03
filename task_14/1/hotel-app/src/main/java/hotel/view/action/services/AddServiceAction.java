package hotel.view.action.services;

import hotel.model.service.Services;
import hotel.service.ServicesService;
import hotel.view.action.BaseAction;

import java.math.BigDecimal;
import java.util.Scanner;

public class AddServiceAction extends BaseAction {
    private final ServicesService servicesService;

    public AddServiceAction(ServicesService servicesService, Scanner scanner) {
        super(scanner);
        this.servicesService = servicesService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ДОБАВЛЕНИЕ УСЛУГИ ===");
            Services service = new Services();
            service.setName(readString("Введите название услуги: "));
            service.setDescription(readString("Введите описание услуги: "));
            service.setPrice(BigDecimal.valueOf(readInt("Введите цену услуги: ", 0, 9999)));
            servicesService.addService(service);
            System.out.println("Услуга успешно добавлена!");
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении услуги: " + e.getMessage());
        }
    }
}