package hotel.service;

import hotel.annotation.Component;
import hotel.annotation.Inject;
import hotel.model.Hotel;
import hotel.model.users.employee.Employee;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Component
@NoArgsConstructor
public class EmployeeService {
    public void addPersonal(Collection<? extends Employee> persons)
    {
        if(persons.isEmpty()){
            System.out.println("Работников нет");
            return;
        }
        for(Employee person : persons){
            Hotel.getInstance().getEmployeeMap().get().put(person.getId(), person);
            System.out.println("Добавили нового члена команды: " + person.toString());
        }
    }
}
