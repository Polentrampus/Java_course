package hotel.repository.employee;

import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HibernateEmployeeRepository extends BaseRepository<Employee, Integer> implements EmployeeRepository {
    public HibernateEmployeeRepository() {
        setEntityClass(Employee.class);
    }

    @Override
    public List<Employee> findByRole(EmployeeRole role) {
        String hql = """
                SELECT e FROM Employee e WHERE e.position = :role
                """;

        return getCurrentSession()
                .createQuery(hql, Employee.class)
                .setParameter("role", role)
                .list();
    }
}
