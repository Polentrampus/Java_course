package hotel.exception;

import java.sql.SQLException;

public class SqlException extends RuntimeException{
    public SqlException(Throwable e) {
        super(e);
    }
    public SqlException(String mes, Throwable e) {
        super(e);
    }

    public SqlException(String mes) {
        super(mes);
    }
}
