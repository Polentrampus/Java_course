package hotel.exception.dao;

import org.hibernate.HibernateException;

public class DAOException extends RuntimeException {
    public DAOException(Exception e) {
        super(e);
    }

    public DAOException(String mes, Exception e) {
        super(e);
    }
}
