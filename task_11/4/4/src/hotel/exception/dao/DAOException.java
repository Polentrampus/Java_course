package hotel.exception.dao;

import hotel.exception.HotelException;

import java.sql.SQLException;

public class DAOException extends RuntimeException {
    public DAOException(SQLException e) {
        super(e);
    }
    public DAOException(String mes, SQLException e) {
        super(e);
    }
}
