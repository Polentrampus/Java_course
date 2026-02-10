package hotel.model.users.employee;

import hotel.service.export_import.CsvImporter;
import hotel.exception.csv.CsvImportLengthException;
import hotel.exception.csv.CsvImportParsingException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeCsvImport implements CsvImporter<Employee> {
    @Override
    public List<Employee> importFromCsv(List<String[]> data) {
        List<Employee> employees = new ArrayList<>();
        for (String[] row : data) {
            try {
                Employee employee = createEntity(row);
                employees.add(employee);
            } catch (Exception e) {
                throw new CsvImportParsingException(row);
            }
        }
        return employees;
    }

    @Override
    public Employee createEntity(String[] data) {
//        if (data.length < 6) {
//            throw new CsvImportLengthException(6, data.length);
//        }
//        return new Employee(
//                Integer.parseInt(data[0]),
//                data[1],
//                data[2],
//                data[3],
//                LocalDate.parse(data[4])
//        ) {
//            @Override
//            public String getPosition() {
//                return data[5];
//            }
//        };
        return null;
    }
    @Override
    public String[] getHeader() {
        return new String[]{"id", "name", "surname", "patronymic", "dateOfBirth", "position", "hire_date", "salary"};
    }
}
