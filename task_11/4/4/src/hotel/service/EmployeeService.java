package hotel.service;

import hotel.annotation.Component;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.repository.employee.EmployeeRepository;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
public class EmployeeService {
    private EmployeeRepository employeeRepository;
    private final EmployeeObserverService employeeObserverService =  new EmployeeObserverService();

    public void setHotelRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        employeeObserverService.setEmployeeRepository(employeeRepository);
    }

    public EmployeeService() {
    }

    public void addPersonal(Collection<? extends Employee> persons)
    {
        if(persons.isEmpty()){
            System.out.println("Работников нет");
            return;
        }
        for(Employee person : persons){
            employeeRepository.save(person);
            System.out.println("Добавили нового члена команды: " + person.toString());
        }
    }

    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public void deleteEmployee(int employeeId) {
        employeeRepository.delete(employeeId);
    }

    public void requestCleaning(int roomId) throws SQLException {
        System.out.println("Запрос на уборку комнаты " + roomId);
        employeeObserverService.notifyCleaningRequest(roomId);
    }

    public void requestRepair(int roomId) throws SQLException {
        System.out.println("Запрос на ремонт комнаты " + roomId);
        employeeObserverService.notifyRepairRequest(roomId);
    }

    public void requestMaintenance(int roomId) throws SQLException {
        System.out.println("Запрос на обслуживание комнаты " + roomId);
        employeeObserverService.notifyMaintenanceRequest(roomId);
    }

    public List<Employee> getAdmins() {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getPosition() == EmployeeRole.ADMIN)
                .toList();
    }

    public List<Employee> getMaids() {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getPosition() == EmployeeRole.MAID)
                .toList();
    }

    public List<Employee> getMenders() {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getPosition() == EmployeeRole.MENDER)
                .toList();
    }
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }
}
