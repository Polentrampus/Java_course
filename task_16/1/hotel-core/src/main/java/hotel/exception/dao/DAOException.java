package hotel.exception.dao;


public class DAOException extends RuntimeException {
    public DAOException(Exception e) {
        super(e);
    }

    public DAOException(String mes, Exception e) {
        super(e);
    }
}
