package hotel.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface HotelRepository<T> {
    Optional<T> findById(int id) throws SQLException;
    List<T> findAll();
    boolean save(T entity);
    boolean update(T entity);
    boolean delete(int id);
}
