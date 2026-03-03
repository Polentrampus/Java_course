package hotel.repository;

import hotel.exception.dao.DAOException;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

@Repository
public abstract class BaseRepository<T, ID> {
    private static final Logger logger = LoggerFactory.getLogger(BaseRepository.class);
    @Setter
    private Class<T> entityClass;
    @Autowired
    private SessionFactory sessionFactory;

    public BaseRepository() {
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    protected void execute(String operationName, Consumer<Session> action, Object... context) {
        try {
            Session session = getCurrentSession();
            action.accept(session);
            logging(operationName, context);
        } catch (Exception e) {
            String errorMsg = getErrorMsg(operationName, context);
            logger.error(errorMsg, e);
            throw new DAOException(e, errorMsg);
        }
    }

    protected <T> T executeWithResult(String operationName, Function<Session, T> action, Object... context) {
        try {
            Session session = getCurrentSession();
            System.out.println("Session obtained in executeWithResult: " + session);
            T t = action.apply(session);
            logging(operationName, context);
            return t;
        } catch (Exception e) {
            String errorMsg = getErrorMsg(operationName, context);
            logger.error(errorMsg, e);
            throw new DAOException(e, errorMsg);
        }
    }

    private static @NonNull String getErrorMsg(String operationName, Object[] context) {
        return String.format("Ошибка операции %s. Контекст: %s", operationName,
                Arrays.toString(context));
    }

    private void logging(String operationName, Object[] context) {
        logger.info("Операция {} выполнена. Контекст: {}", operationName,
                Arrays.toString(context));
    }

}
