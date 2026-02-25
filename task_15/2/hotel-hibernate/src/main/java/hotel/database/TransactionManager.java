package hotel.database;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TransactionManager {
    private SessionFactory sessionFactory;
    private static Logger log = LoggerFactory.getLogger(TransactionManager.class);

    @Autowired
    private HibernateUtil hibernateUtil;

    public void init() {
        System.out.println("init method transactionManager");
        this.sessionFactory = hibernateUtil.getSessionFactory();
    }

    public TransactionManager() {}

    public TransactionManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
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
