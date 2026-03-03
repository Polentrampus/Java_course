package hotel.view.action.employee;


import hotel.service.ClientService;
import hotel.service.CsvImportService;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeCsvImport;
import hotel.service.EmployeeService;
import hotel.view.action.BaseAction;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class CsvImportEmployeeActon extends BaseAction {
    private final EmployeeService employeeService;
    private final CsvImportService importManager;

    public CsvImportEmployeeActon(EmployeeService employeeService, Scanner scanner) {
        super(scanner);
        this.employeeService = employeeService;
        this.importManager = new CsvImportService();
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ИМПОРТ РАБОТНИКОВ ===");

            String filePath = readString("Введите путь к CSV файлу: " +
                    "( C:\\my_program\\Java_course\\task_6\\1\\exports\\employeesExport.csv )");
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("Файл не найден: " + filePath);
                return;
            }

            System.out.println("Импорт из файла: " + file.getName());
            EmployeeCsvImport importer = new EmployeeCsvImport();
            List<Employee> importedRooms = importManager.importFromFile(file, importer);

            if (importedRooms.isEmpty()) {
                System.out.println("Не удалось импортировать комнаты из файла");
                return;
            }

            System.out.println("Успешно импортированы работники: " + importedRooms.size());
            employeeService.addPersonal(importedRooms);
        } catch (Exception e) {
            System.out.println("Ошибка при импорте работников: " + e.getMessage());
        }
    }

}
