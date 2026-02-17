package hotel.view.action.employee;


import hotel.service.EmployeeService;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class CleaningRequestAction extends BaseAction {
    private final EmployeeService employeeService;

    public CleaningRequestAction(EmployeeService employeeService, Scanner scanner) {
        super(scanner);
        this.employeeService = employeeService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ЗАПРОС НА УБОРКУ ===");
            int roomId = readInt("Введите ID комнаты для уборки: ");
            System.out.println("Запрос на уборку отправлен!");
            employeeService.requestCleaning(roomId);
        } catch (Exception e) {
            System.out.println("Ошибка при отправке запроса на уборку: " + e.getMessage());
        }
    }
}