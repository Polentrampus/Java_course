package hotel.view.action.employee;

import hotel.controller.AdminController;
import hotel.controller.manager.CsvExportManager;
import hotel.users.employee.Employee;
import hotel.users.employee.EmployeeCsvExport;
import hotel.view.action.BaseAction;

import java.util.List;
import java.util.Scanner;

public class CsvExportEmployeeAction extends BaseAction {
    AdminController adminController;
    CsvExportManager csvExportManager;

    public CsvExportEmployeeAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
        this.csvExportManager = new CsvExportManager();
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ЭКСПОРТ РАБОТНИКОВ ===");

            List<Employee> employees = adminController.getEmployees();

            if (employees.isEmpty()) {
                System.out.println("Нет Работников для экспорта!");
                return;
            }

            System.out.println("Найдены работники для экспорта: " + employees.size());
            EmployeeCsvExport exporter = new EmployeeCsvExport();
            csvExportManager.exportToFile("exports", employees, exporter);

        } catch (Exception e) {
            System.out.println("Ошибка при экспорте работников: " + e.getMessage());
        }
    }
}
