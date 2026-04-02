package hotel.view.action.employee;

import hotel.model.users.employee.Employee;
import hotel.service.EmployeeService;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class EmployeeListAction extends BaseAction {
    private final EmployeeService employeeService;

    public EmployeeListAction(EmployeeService employeeService, Scanner scanner) {
        super(scanner);
        this.employeeService = employeeService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== СПИСОК РАБОТНИКОВ ===");
            for (Employee employee : employeeService.findAll()) {
                System.out.println(employee);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при запросе списка всех работников: " + e.getMessage());
        }
    }
}