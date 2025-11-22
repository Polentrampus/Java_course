package hotel.controller.manager;

import hotel.model.Hotel;
import hotel.users.employee.Employee;

import java.util.ArrayList;
import java.util.Collection;

public class EmployeeManager {
    private Employee employee;

    public EmployeeManager(Employee employee) {
        this.employee = employee;
    }

    public void addPersonal(Collection<? extends Employee> persons)
    {
        if(Hotel.getInstance().getEmployeeMap().isEmpty()){
            System.out.println("Работников нет");
            return;
        }
        for(Employee person : persons){
            Hotel.getInstance().getEmployeeMap().get().put(person.getId(), person);
            System.out.println(employee.getPosition() + " нового члена команды: " + person.toString());
        }
    }
}
