package hotel.dao;

import hotel.exception.dao.DAOException;
import hotel.model.users.employee.Employee;
import hotel.model.users.employee.EmployeeRole;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAO implements BaseDAO<Employee> {
    private static final EmployeeDAO INSTANCE = new EmployeeDAO();

    private final static String FIND_BY_ID_SQL = """
        SELECT p.*, e.position::text as position, e.hire_date, e.salary 
        FROM persons p 
        JOIN employees e ON p.id = e.id 
        WHERE p.id = ?
        """;

    private final static String FIND_ALL_SQL = """
        SELECT p.*, e.position::text as position, e.hire_date, e.salary 
        FROM persons p 
        JOIN employees e ON p.id = e.id
        """;

    private final static String SAVE_SQL = """
        WITH inserted_person AS (
            INSERT INTO persons (name, surname, patronymic, date_of_birth)
            VALUES (?, ?, ?, ?)
            RETURNING id
        )
        INSERT INTO employees (id, position, hire_date, salary)
        SELECT id, ?::position_type, ?, ?
        FROM inserted_person
        """;

    private final static String UPDATE_SQL = """
        UPDATE persons SET 
            name = ?, 
            surname = ?,
            patronymic = ?,
            date_of_birth = ?
        WHERE id = ?;
        
        UPDATE employees SET
            position = ?::position_type,
            hire_date = ?,
            salary = ?
        WHERE id = ?;
        """;

    private final static String DELETE_SQL = """
        DELETE FROM persons WHERE id = ?;
        """;

    public static EmployeeDAO getInstance() {
        return INSTANCE;
    }

    private EmployeeDAO() {}

    @Override
    public Optional<Employee> findById(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(buildEmployee(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("Failed to find employee by id: " + id, e);
        }
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(buildEmployee(rs));
            }
            return employees;
        } catch (SQLException e) {
            throw new DAOException("Failed to get all employees", e);
        }
    }

    @Override
    public boolean save(Employee employee) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getSurname());
            stmt.setString(3, employee.getPatronymic());
            stmt.setDate(4, Date.valueOf(employee.getDate_of_birth()));

            // Параметры для employees
            stmt.setString(5, employee.getPosition().name());
            stmt.setDate(6, Date.valueOf(employee.getHireDate()));
            stmt.setDouble(7, employee.getSalary());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to save employee", e);
        }
        return true;
    }

    @Override
    public boolean update(Employee employee) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getSurname());
            stmt.setString(3, employee.getPatronymic());
            stmt.setDate(4, Date.valueOf(employee.getDate_of_birth()));
            stmt.setInt(5, employee.getId());

            stmt.setString(6, employee.getPosition().name());
            stmt.setDate(7, Date.valueOf(employee.getHireDate()));
            stmt.setDouble(8, employee.getSalary());
            stmt.setInt(9, employee.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to update employee", e);
        }
        return true;
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to delete employee", e);
        }
        return true;
    }

    private Employee buildEmployee(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String surname = rs.getString("surname");
        String patronymic = rs.getString("patronymic");
        LocalDate dateOfBirth = rs.getDate("date_of_birth").toLocalDate();

        String positionStr = rs.getString("position");
        EmployeeRole position = EmployeeRole.valueOf(positionStr);
        LocalDate hireDate = rs.getDate("hire_date").toLocalDate();
        double salary = rs.getDouble("salary");

        return new Employee(id, name, surname, patronymic, dateOfBirth,
                position, hireDate, salary);
    }

    public List<Employee> findByPosition(EmployeeRole position) throws SQLException {
        String sql = FIND_ALL_SQL + " WHERE e.position = ?::position_type";
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, position.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(buildEmployee(rs));
            }
            return employees;
        } catch (SQLException e) {
            throw new DAOException("Failed to find employees by position", e);
        }
    }
}
