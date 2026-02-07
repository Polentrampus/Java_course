package hotel.dao;

import hotel.exception.dao.DAOException;
import hotel.model.users.client.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDao implements BaseDAO<Client> {
    private final static ClientDao INSTANCE = new ClientDao();
    private final static String FIND_BY_ID_SQL = """
            select p.*, c.notes from persons p 
            join clients c on p.id = c.id 
            WHERE p.id = ? and p.type = 'client'
            """;
    private final static String FIND_ALL_SQL = """
            select p.*, c.notes from persons p join clients c on p.id = c.id WHERE p.type = 'client'
            """;
    private final static String SAVE_SQL = """
            with inserted_person as (
            insert into persons
            (type, name, surname, patronymic, date_of_birth)
            values ('client', ?, ?, ?, ?)
            returning id
            )
            insert into clients (id, notes)
            select id, ? from inserted_person
            """;
    private final static String UPDATE_SQL = """
            WITH updated_person AS (
                        UPDATE persons SET
                            name = ?,
                            surname = ?,
                            patronymic = ?,
                            date_of_birth = ?,
                            updated_at = CURRENT_TIMESTAMP
                        WHERE id = ? AND type = 'client'
                        RETURNING id
                    )
                    UPDATE clients SET
                        notes = ?
                    WHERE id = (SELECT id FROM updated_person)
            """;
    private final static String DELETE_SQL = "delete from persons where id = ? and type = 'client'";

    public static ClientDao getInstance() {
        return INSTANCE;
    }

    private ClientDao() {
    }

    @Override
    public Optional<Client> findById(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(buildClient(rs));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Client client = buildClient(rs);
                clients.add(client);
            }
            return clients;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean save(Client client) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getSurname());
            stmt.setString(3, client.getPatronymic());
            stmt.setDate(4, Date.valueOf(client.getDate_of_birth()));
            stmt.setString(5, client.getNotes());

            int curRow = stmt.executeUpdate();
            if (curRow > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        client.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return false;
    }

    @Override
    public boolean update(Client client) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, client.getName());
            stmt.setString(2, client.getSurname());
            stmt.setString(3, client.getPatronymic());
            stmt.setDate(4, Date.valueOf(client.getDate_of_birth()));
            stmt.setInt(5, client.getId());
            stmt.setString(6, client.getNotes());
            stmt.setInt(7, client.getId());
            int curRow = stmt.executeUpdate();
            if (curRow > 0) {
                System.out.println(client.getName() + " has been updated");
                return true;
            }

        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            int rowDel = stmt.executeUpdate();
            if (rowDel > 0) {
                System.out.println(id + " has been deleted");
                return true;
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return false;
    }

    private Client buildClient(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setId(rs.getInt("id"));
        client.setName(rs.getString("name"));
        client.setSurname(rs.getString("surname"));

        String patronymic = rs.getString("patronymic");
        client.setPatronymic(rs.wasNull() ? null : patronymic);

        Date date = rs.getDate("date_of_birth");
        client.setDate_of_birth(date != null ? date.toLocalDate() : null);

        client.setNotes(rs.getString("notes"));

        return client;
    }
}
