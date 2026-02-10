package hotel.dao;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.dao.DAOException;
import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.model.service.Services;
import hotel.model.users.client.Client;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class BookingDAO implements BaseDAO<Bookings> {
    private final static BookingDAO INSTANCE = new BookingDAO();

    private final static String FIND_BY_ID_SQL = """
            SELECT b.*,
                   c.id as client_id, p.name as client_name, p.surname as client_surname,
                   p.patronymic as client_patronymic, p.date_of_birth as client_birth,
                   r.number as room_number, r.category, r.status as room_status, r.type, r.capacity, r.price as room_price
            FROM bookings b
            JOIN clients c ON b.client_id = c.id
            JOIN persons p ON c.id = p.id
            JOIN rooms r ON b.room_number = r.number
            WHERE b.id = ?
            """;

    private final static String FIND_ALL_SQL = """
            SELECT b.*,
                   c.id as client_id, p.name as client_name, p.surname as client_surname,
                   p.patronymic as client_patronymic, p.date_of_birth as client_birth,
                   r.number as room_number, r.category, r.status as room_status, r.type, r.capacity, r.price as room_price
            FROM bookings b
            JOIN clients c ON b.client_id = c.id
            JOIN persons p ON c.id = p.id
            JOIN rooms r ON b.room_number = r.number
            ORDER BY b.created_at DESC
            """;

    private final static String SAVE_SQL = """
            INSERT INTO bookings
            (client_id, room_number, check_in_date, check_out_date, total_price, status)
            VALUES (?, ?, ?, ?, ?, ?::booking_status_type)
            """;

    private final static String UPDATE_SQL = """
            UPDATE bookings SET
            client_id = ?,
            room_number = ?,
            check_in_date = ?,
            check_out_date = ?,
            total_price = ?,
            status = ?::booking_status_type,
            updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

    private final static String DELETE_SQL = "DELETE FROM bookings WHERE id = ?";

    private final static String FIND_ACTIVE_BOOKINGS_SQL = """
            SELECT b.*,
                   c.id as client_id, p.name as client_name, p.surname as client_surname,
                   p.patronymic as client_patronymic, p.date_of_birth as client_birth,
                   r.number as room_number, r.category, r.status as room_status, r.type, r.capacity, r.price as room_price
            FROM bookings b
            JOIN clients c ON b.client_id = c.id
            JOIN persons p ON c.id = p.id
            JOIN rooms r ON b.room_number = r.number
            WHERE b.status = 'CONFIRMED'
            AND ? BETWEEN b.check_in_date AND b.check_out_date
            """;

    private final static String FIND_BY_ROOM_ID_SQL = """
            SELECT b.*,
                   c.id as client_id, p.name as client_name, p.surname as client_surname,
                   p.patronymic as client_patronymic, p.date_of_birth as client_birth,
                   r.number as room_number, r.category, r.status as room_status, r.type, r.capacity, r.price as room_price
            FROM bookings b
            JOIN clients c ON b.client_id = c.id
            JOIN persons p ON c.id = p.id
            JOIN rooms r ON b.room_number = r.number
            WHERE b.room_number = ?
            ORDER BY b.check_in_date
            """;

    private final static String FIND_BY_CLIENT_ID_SQL = """
            SELECT b.*,
                   c.id as client_id, p.name as client_name, p.surname as client_surname,
                   p.patronymic as client_patronymic, p.date_of_birth as client_birth,
                   r.number as room_number, r.category, r.status  as room_status, r.type, r.capacity, r.price as room_price
            FROM bookings b
            JOIN clients c ON b.client_id = c.id
            JOIN persons p ON c.id = p.id
            JOIN rooms r ON b.room_number = r.number
            WHERE b.client_id = ?
            ORDER BY b.check_in_date DESC
            """;

    private final static String FIND_ACTIVE_BY_ROOM_ID_SQL = """
            SELECT b.*,
                   c.id as client_id, p.name as client_name, p.surname as client_surname,
                   p.patronymic as client_patronymic, p.date_of_birth as client_birth,
                   r.number as room_number, r.category, r.status as room_status, r.type, r.capacity, r.price as room_price
            FROM bookings b
            JOIN clients c ON b.client_id = c.id
            JOIN persons p ON c.id = p.id
            JOIN rooms r ON b.room_number = r.number
            WHERE b.room_number = ?
            AND b.status = 'CONFIRMED'
            AND ? BETWEEN b.check_in_date AND b.check_out_date
            LIMIT 1
            """;

    private final static String ADD_SERVICE_TO_BOOKING_SQL = """
            INSERT INTO booking_services (booking_id, service_id, service_price)
            VALUES (?, ?, ?)
            """;

    private final static String REMOVE_SERVICE_FROM_BOOKING_SQL = """
            DELETE FROM booking_services
            WHERE booking_id = ? AND service_id = ?
            """;

    private final static String GET_BOOKING_SERVICES_SQL = """
            SELECT s.*, bs.service_price
            FROM booking_services bs
            JOIN services s ON bs.service_id = s.id
            WHERE bs.booking_id = ?
            """;

    private final ServiceDAO serviceDAO = ServiceDAO.getInstance();
    private final RoomDAO roomDAO = RoomDAO.getInstance();
    private final ClientDao clientDAO = ClientDao.getInstance();

    public static BookingDAO getInstance() {
        return INSTANCE;
    }

    private BookingDAO() {
    }

    @Override
    public Optional<Bookings> findById(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Bookings booking = buildBooking(rs);
                booking.setServices(getBookingServices(conn, id));
                return Optional.of(booking);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Bookings> findAll() {
        List<Bookings> bookings = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Bookings booking = buildBooking(rs);
                booking.setServices(getBookingServices(conn, booking.getId()));
                bookings.add(booking);
            }
            return bookings;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public List<Bookings> findActiveBookings(LocalDate date) {
        List<Bookings> bookings = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ACTIVE_BOOKINGS_SQL)) {

            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Bookings booking = buildBooking(rs);
                booking.setServices(getBookingServices(conn, booking.getId()));
                bookings.add(booking);
            }
            return bookings;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public List<Bookings> findByRoomId(int roomId) {
        List<Bookings> bookings = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ROOM_ID_SQL)) {

            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Bookings booking = buildBooking(rs);
                booking.setServices(getBookingServices(conn, booking.getId()));
                bookings.add(booking);
            }
            return bookings;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public List<Bookings> findByClientId(int clientId) {
        List<Bookings> bookings = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_CLIENT_ID_SQL)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Bookings booking = buildBooking(rs);
                booking.setServices(getBookingServices(conn, booking.getId()));
                bookings.add(booking);
            }
            return bookings;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public Optional<Bookings> findActiveByRoomId(int roomId, LocalDate date) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ACTIVE_BY_ROOM_ID_SQL)) {

            stmt.setInt(1, roomId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Bookings booking = buildBooking(rs);
                booking.setServices(getBookingServices(conn, booking.getId()));
                return Optional.of(booking);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean save(Bookings booking) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Начинаем транзакцию

            try (PreparedStatement stmt = conn.prepareStatement(SAVE_SQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, booking.getClient());
                stmt.setInt(2, booking.getRoom());
                stmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
                stmt.setDate(4, Date.valueOf(booking.getCheckOutDate()));
                stmt.setBigDecimal(5, booking.getTotalPrice());
                stmt.setString(6, booking.getStatus().name());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            booking.setId(rs.getInt(1));
                        }
                    }
                }
            }
            saveBookingServices(conn, booking);
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new HotelException(ErrorCode.DATABASE_TRANSACTION_ERROR, ex.getMessage());
                }
            }
            throw new DAOException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean update(Bookings booking) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
                stmt.setInt(1, booking.getClient());
                stmt.setInt(2, booking.getRoom());
                stmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
                stmt.setDate(4, Date.valueOf(booking.getCheckOutDate()));
                stmt.setBigDecimal(5, booking.getTotalPrice());
                stmt.setString(6, booking.getStatus().name());
                stmt.setInt(7, booking.getId());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            updateBookingServices(conn, booking);
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new HotelException(ErrorCode.DATABASE_TRANSACTION_ERROR, ex.getMessage());
                }
            }
            throw new DAOException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /// Добавляем услуги
    private void saveBookingServices(Connection conn, Bookings booking) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(ADD_SERVICE_TO_BOOKING_SQL)) {
            for (Services service : booking.getServices()) {
                stmt.setInt(1, booking.getId());
                stmt.setInt(2, service.getId());
                stmt.setBigDecimal(3, service.getPrice());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /// Обновляем услуги
    private void updateBookingServices(Connection conn, Bookings booking) throws SQLException {
        try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM booking_services WHERE booking_id = ?")) {
            deleteStmt.setInt(1, booking.getId());
            deleteStmt.executeUpdate();
        }

        saveBookingServices(conn, booking);
    }

    private List<Services> getBookingServices(Connection conn, int bookingId) throws SQLException {
        List<Services> services = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(GET_BOOKING_SERVICES_SQL)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Services service = new Services();
                service.setId(rs.getInt("id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getBigDecimal("service_price"));
                services.add(service);
            }
        }
        return services;
    }

    private Bookings buildBooking(ResultSet rs) throws SQLException {
        Bookings booking = new Bookings();
        booking.setId(rs.getInt("id"));
        booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
        booking.setTotalPrice(rs.getBigDecimal("total_price"));
        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
        booking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        booking.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        Client client = new Client();
        client.setId(rs.getInt("client_id"));
        client.setName(rs.getString("client_name"));
        client.setSurname(rs.getString("client_surname"));
        client.setPatronymic(rs.getString("client_patronymic"));
        Date clientBirth = rs.getDate("client_birth");
        client.setDateOfBirth(clientBirth != null ? clientBirth.toLocalDate() : null);
        booking.setClient(client.getId());

        Room room = new Room();
        room.setNumber(rs.getInt("room_number"));
        room.setCategory(RoomCategory.valueOf(rs.getString("category")));
        room.setStatus(RoomStatus.valueOf(rs.getString("room_status")));
        room.setType(RoomType.valueOf(rs.getString("type")));
        room.setCapacity(rs.getInt("capacity"));
        room.setPrice(rs.getBigDecimal("room_price"));
        booking.setRoom(room.getId());

        return booking;
    }
}
