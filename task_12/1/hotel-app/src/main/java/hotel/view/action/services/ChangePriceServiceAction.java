package hotel.view.action.services;


import hotel.service.ServicesService;
import hotel.view.action.BaseAction;

import java.math.BigDecimal;
import java.util.Scanner;

public class ChangePriceServiceAction extends BaseAction {
    private final ServicesService servicesService;

    public ChangePriceServiceAction(ServicesService servicesService, Scanner scanner) {
        super(scanner);
        this.servicesService = servicesService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ИЗМЕНЕНИЕ ЦЕНЫ УСЛУГИ ===");
            String name = readString("Введите название услуги: ");
            BigDecimal price = BigDecimal.valueOf(readInt("Введите новую цену: ", 0, 9999));

            servicesService.setPrice(name, price);
            System.out.println("Цена услуги успешно изменена!");
        } catch (Exception e) {
            System.out.println("Ошибка при изменении цены услуги: " + e.getMessage());
        }
    }
}