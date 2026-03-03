package hotel.dao;

import hotel.exception.dao.DAOException;
import hotel.model.users.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonDAO implements BaseDAO<Person> {
    private final static PersonDAO INSTANCE = new PersonDAO();
    private final static String FIND_BY_ID_SQL = "select * from persons WHERE id = ?";
    private final static String FIND_ALL_SQL = "select * from persons";
    private final static String SAVE_SQL = """
            insert into persons
            (type, name, surname, patronymic, date_of_birth)
            values
            (?,?,?,?,?)
            """;
    private final static String UPDATE_SQL = """
            update persons set
             type = ?,
             name = ?,
             surname = ?,
             patronymic = ?,
             date_of_birth = ?,
             updated_at = CURRENT_TIMESTAMP
             WHERE id = ?
            """;
    private final static String DELETE_SQL = "delete from persons WHERE id = ?";

    public static PersonDAO getInstance() {
        return INSTANCE;
    }

    private PersonDAO() {
    }
    @Override
    public Optional<Person> findById(int id) throws SQLException {
        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of(buildPerson(rs));
            }
        }catch(SQLException e){
            throw new DAOException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Person> findAll() {
        List<Person> persons = new ArrayList<>();
        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Person person = buildPerson(rs);
                persons.add(person);
            }
            return persons;
        }catch(SQLException e){
            throw new DAOException(e);
        }
    }

    @Override
    public boolean save(Person person) {
        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1,person.getName());
            stmt.setString(2,person.getSurname());
            stmt.setString(3,person.getPatronymic());
            stmt.setDate(4, Date.valueOf(person.getDate_of_birth()));
            int curRow = stmt.executeUpdate();
            if(curRow > 0){
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()){
                        person.setId(rs.getInt(1));
                        return  true;
                    }
                }
            }
        }catch(SQLException e){
            throw new DAOException(e);
        }
        return false;
    }

    @Override
    public boolean update(Person person) {
        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)){

            stmt.setString(1,person.getName());
            stmt.setString(2,person.getSurname());
            stmt.setString(3,person.getPatronymic());
            stmt.setDate(4, Date.valueOf(person.getDate_of_birth()));
            stmt.setInt(5,person.getId());
            int curRow = stmt.executeUpdate();
            if(curRow > 0){
                System.out.println(person.getName() + " has been updated");
                return true;
            }

        }catch(SQLException e){
            throw new DAOException(e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)){
            stmt.setInt(1, id);
            int rowDel = stmt.executeUpdate();
            if(rowDel > 0){
                System.out.println(id + " has been deleted");
                return true;
            }
        }catch(SQLException e){
            throw new DAOException(e);
        }
        return false;
    }
    private Person buildPerson(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setId(rs.getInt("id"));
        person.setName(rs.getString("name"));
        person.setSurname(rs.getString("surname"));

        String patronymic = rs.getString("patronymic");
        person.setPatronymic(rs.wasNull() ? null : patronymic);

        Date date = rs.getDate("date_of_birth");
        person.setDate_of_birth(date != null ? date.toLocalDate() : null);
        return person;
    }
}
