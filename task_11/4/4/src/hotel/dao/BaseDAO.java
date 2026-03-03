package hotel.dao;

import hotel.service.export_import.Entity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BaseDAO<T extends Entity> {
    Optional<T> findById(int id) throws SQLException;
    List<T> findAll();
    boolean save(T entity);
    boolean update(T entity);
    boolean delete(int id);

    default Connection getConnection() throws SQLException {
        return hotel.util.DatabaseConnection.getConnection();
    }
}