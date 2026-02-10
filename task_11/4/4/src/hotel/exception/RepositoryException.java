package hotel.exception;

public class RepositoryException extends RuntimeException{
    public RepositoryException(Throwable e) {
        super(e);
    }
    public RepositoryException(String mes, Throwable e) {
        super(e);
    }
    public RepositoryException(String mes) {
        super();
    }

}
