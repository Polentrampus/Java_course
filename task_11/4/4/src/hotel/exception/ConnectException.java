package hotel.exception;

import java.sql.SQLException;

public class ConnectException extends RuntimeException{
    public ConnectException(SQLException e) {
        super(e);
    }
}
