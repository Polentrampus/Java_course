package hotel.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface HotelRepository<T> {
    Optional<T> findById(Integer id) throws SQLException;
    List<T> findAll();
    Integer save(T entity);
    void update(T entity);
    void delete(T id);
}