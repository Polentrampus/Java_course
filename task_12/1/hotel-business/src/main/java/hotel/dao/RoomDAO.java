package hotel.dao;

import hotel.exception.dao.DAOException;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class RoomDAO implements BaseDAO<Room> {
    private final static RoomDAO INSTANCE = new RoomDAO();
    private final static String FIND_BY_ID_SQL = "select * from rooms WHERE number = ?";
    private final static String FIND_ALL_SQL = "select * from rooms";
    private final static String SAVE_SQL = """
            insert into rooms
            (number, category, status, type, capacity, price)
            values
            (?,?,?,?,?,?)
            """;
    private final static String UPDATE_SQL = """
            update rooms set
             number = ?,
             category = ?,
             status = ?,
             type = ?,
             capacity = ?,
             price = ? WHERE number = ?
            """;
    private final static String DELETE_SQL = "delete from rooms WHERE number = ?";

    public static RoomDAO getInstance() {
        return INSTANCE;
    }

    private RoomDAO() {
    }

    @Override
    public Optional<Room> findById(int number) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setInt(1, number);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(buildRoom(rs));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = buildRoom(rs);
                rooms.add(room);
            }
            return rooms;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean save(Room room) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SAVE_SQL)) {
            stmt.setInt(1, room.getNumber());
            stmt.setObject(2, room.getCategory(), Types.OTHER);
            stmt.setObject(3, room.getStatus(), Types.OTHER);
            stmt.setObject(4, room.getType(), Types.OTHER);
            stmt.setInt(5, room.getCapacity());
            stmt.setBigDecimal(6, room.getPrice());
            int curRow = stmt.executeUpdate();
            if (curRow > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return false;
    }

    @Override
    public boolean update(Room room) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setInt(1, room.getNumber());
            stmt.setObject(2, room.getCategory(), Types.OTHER);
            stmt.setObject(3, room.getStatus(), Types.OTHER);
            stmt.setObject(4, room.getType(), Types.OTHER);
            stmt.setInt(5, room.getCapacity());
            stmt.setBigDecimal(6, room.getPrice());
            int curRow = stmt.executeUpdate();
            if (curRow > 0) {
                System.out.println(room.getId() + " has been updated");
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

    private Room buildRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setNumber(rs.getInt("number"));
        room.setCategory(RoomCategory.valueOf(rs.getString("category")));
        room.setStatus(RoomStatus.valueOf(rs.getString("status")));
        room.setType(RoomType.valueOf(rs.getString("type")));
        room.setCapacity(rs.getInt("capacity"));
        room.setPrice(rs.getBigDecimal("price"));
        return room;
    }
}
