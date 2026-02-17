package hotel.view.action.employee;


import hotel.service.ClientService;
import hotel.service.CsvExportService;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeCsvExport;
import hotel.service.EmployeeService;
import hotel.view.action.BaseAction;

import java.util.List;
import java.util.Scanner;

public class CsvExportEmployeeAction extends BaseAction {
    private final EmployeeService employeeService;
    private final CsvExportService csvExportService;

    public CsvExportEmployeeAction(EmployeeService employeeService, Scanner scanner) {
        super(scanner);
        this.employeeService = employeeService;
        this.csvExportService = new CsvExportService();
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ЭКСПОРТ РАБОТНИКОВ ===");

            List<Employee> employees = employeeService.findAll();

            if (employees.isEmpty()) {
                System.out.println("Нет Работников для экспорта!");
                return;
            }

            System.out.println("Найдены работники для экспорта: " + employees.size());
            EmployeeCsvExport exporter = new EmployeeCsvExport();
            csvExportService.exportToFile("exports", employees, exporter);

        } catch (Exception e) {
            System.out.println("Ошибка при экспорте работников: " + e.getMessage());
        }
    }
}
