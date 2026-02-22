package hotel.view.action.employee;


import hotel.service.EmployeeService;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class RepairRequestAction extends BaseAction {
    private final EmployeeService employeeService;

    public RepairRequestAction(EmployeeService employeeService, Scanner scanner) {
        super(scanner);
        this.employeeService = employeeService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ЗАПРОС НА РЕМОНТ ===");
            int roomId = readInt("Введите ID комнаты для ремонта: ");
            System.out.println("Запрос на ремонт отправлен!");
            employeeService.requestRepair(roomId);
        } catch (Exception e) {
            System.out.println("Ошибка при отправке запроса на ремонт: " + e.getMessage());
        }
    }
}