package hotel.dao;

import hotel.exception.dao.DAOException;
import hotel.model.service.Services;

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

public final class ServiceDAO implements BaseDAO<Services> {
    private final static ServiceDAO INSTANCE = new ServiceDAO();

    private final static String FIND_BY_ID_SQL = "SELECT * FROM services WHERE id = ?";
    private final static String FIND_ALL_SQL = "SELECT * FROM services ORDER BY name";
    private final static String FIND_BY_NAME_SQL = "SELECT * FROM services WHERE name = ?";
    private final static String SAVE_SQL = """
            INSERT INTO services (name, description, price)
            VALUES (?, ?, ?)
            """;
    private final static String UPDATE_SQL = """
            UPDATE services SET
            name = ?,
            description = ?,
            price = ?,
            WHERE id = ?
            """;
    private final static String DELETE_SQL = "DELETE FROM services WHERE id = ?";

    // Для статистики
    private final static String GET_MOST_POPULAR_SERVICES_SQL = """
            SELECT s.*, COUNT(bs.service_id) as usage_count
            FROM services s
            LEFT JOIN booking_services bs ON s.id = bs.service_id
            GROUP BY s.id
            ORDER BY usage_count DESC
            LIMIT ?
            """;

    public static ServiceDAO getInstance() {
        return INSTANCE;
    }

    private ServiceDAO() {
    }

    @Override
    public Optional<Services> findById(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(buildService(rs));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Services> findAll() {
        List<Services> services = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                services.add(buildService(rs));
            }
            return services;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public Optional<Services> findByName(String name) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_NAME_SQL)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(buildService(rs));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return Optional.empty();
    }

    public List<Services> findMostPopularServices(int limit) {
        List<Services> services = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_MOST_POPULAR_SERVICES_SQL)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Services service = buildService(rs);
                services.add(service);
            }
            return services;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean save(Services service) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, service.getName());
            stmt.setString(2, service.getDescription());
            stmt.setBigDecimal(3, service.getPrice());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        service.setId(rs.getInt(1));
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
    public boolean update(Services service) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, service.getName());
            stmt.setString(2, service.getDescription());
            stmt.setBigDecimal(3, service.getPrice());
            stmt.setInt(4, service.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException(e);
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

    public List<Services> getServicesByBookingId(int bookingId) {
        List<Services> services = new ArrayList<>();
        String sql = """
                SELECT s.*, bs.service_price
                FROM booking_services bs
                JOIN services s ON bs.service_id = s.id
                WHERE bs.booking_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Services service = buildService(rs);
                service.setPrice(rs.getBigDecimal("service_price"));
                services.add(service);
            }
            return services;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public double getTotalServiceRevenue(LocalDate startDate, LocalDate endDate) {
        String sql = """
                SELECT COALESCE(SUM(bs.service_price), 0) as total_revenue
                FROM booking_services bs
                JOIN bookings b ON bs.booking_id = b.id
                WHERE b.created_at BETWEEN ? AND ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }
            return 0.0;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private Services buildService(ResultSet rs) throws SQLException {
        Services service = new Services();
        service.setId(rs.getInt("id"));
        service.setName(rs.getString("name"));
        service.setDescription(rs.getString("description"));
        service.setPrice(rs.getBigDecimal("price"));
        return service;
    }
}