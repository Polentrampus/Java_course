package hotel.repository.employee;

import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;
import hotel.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HibernateEmployeeRepository extends BaseRepository<Employee, Integer> implements EmployeeRepository {

    public HibernateEmployeeRepository() {
        setEntityClass(Employee.class);
    }

    @Override
    public Optional<Employee> findById(Integer id) {
        return executeWithResult("findById",
                session -> Optional.ofNullable(session.get(Employee.class, id)),
                "id", id
        );
    }

    @Override
    public List<Employee> findAll() {
        return executeWithResult("findAll",
                session -> session.createQuery("from Employee", Employee.class).list()
        );
    }

    @Override
    public Integer save(Employee entity) {
        return executeWithResult("save",
                session -> (Integer) session.save(entity),
                "employeeName", entity.getName(),
                "position", entity.getPosition()
        );
    }

    @Override
    public void update(Employee entity) {
        execute("update",
                session -> session.update(entity),
                "employeeId", entity.getId()
        );
    }

    @Override
    public void delete(Employee entity) {
        execute("delete",
                session -> session.delete(entity),
                "employeeId", entity.getId()
        );
    }

    @Override
    public List<Employee> findByRole(EmployeeRole role) {
        return executeWithResult("findByRole",
                session -> {
                    String hql = "SELECT e FROM Employee e WHERE e.position = :role";
                    return session.createQuery(hql, Employee.class)
                            .setParameter("role", role)
                            .list();
                },
                "role", role
        );
    }
}