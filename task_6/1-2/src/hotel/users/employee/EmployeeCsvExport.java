package hotel.users.employee;

import hotel.controller.export_import.CsvExporter;
import hotel.model.service.Services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeCsvExport implements CsvExporter<Employee> {
    @Override
    public List<String[]> exportCSV(List<Employee> employees) {
        List<String[]> employeesList = new ArrayList<>();
        for (Employee employee : employees) {
            String[] csvData = {
                    String.valueOf(employee.getId()),
                    employee.getName(),
                    employee.getSurname(),
                    employee.getPatronymic(),
                    String.valueOf(employee.getDate_of_birth()),
                    employee.getPosition()
            };
            employeesList.add(csvData);
        }
        return employeesList;
    }

    @Override
    public String[] getHeader() {
        return new String[]{"id", "name", "surname", "patronymic", "birthday", "position"};
    }

    @Override
    public String fileName() {
        return "employeesExport.csv";
    }
}
