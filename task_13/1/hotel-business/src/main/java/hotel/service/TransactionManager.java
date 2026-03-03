package hotel.service;

import hotel.annotation.Component;
import hotel.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TransactionManager {
    private SessionFactory sessionFactory;
    private static Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    public  TransactionManager() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public TransactionManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T executeInTransaction(TransactionCallback<T> action) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            SessionContext.setSession(session);

            T result = action.execute();

            transaction.commit();
            return result;

        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (HibernateException ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
            throw new RuntimeException("Transaction failed", e);

        } finally {
            SessionContext.clear();
            if (session != null) {
                session.close();
            }
        }
    }

    @FunctionalInterface
    public interface TransactionCallback<T> {
        T execute() throws Exception;
    }
}
