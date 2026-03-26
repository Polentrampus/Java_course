package service;

import model.Account;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {
    private final SessionFactory sessionFactory;
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AccountService.class);
    private final Random random = new Random();

    public AccountService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public boolean isAccountsTableEmpty() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "select count(a) from Account a";
            Long count = (Long) session.createQuery(hql, Long.class).uniqueResult();
            return count == 0;
        }
    }

    public void generateAndSaveAccounts(int count) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            for (int i = 0; i < count; i++) {
                double initialBalance = 5000 + random.nextDouble() * 5000;
                Account account = new Account((long) i, BigDecimal.valueOf(Math.round(initialBalance * 100.0) / 100.0));
                session.persist(account);
            }
            transaction.commit();
            log.info("Создано {} счетов", count);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Ошибка при создании счетов", e);
            throw new RuntimeException("Ошибка при создании счетов", e);
        }
    }

    public List<Account> findAllAccounts() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from Account";
            return session.createQuery(hql, Account.class).list();
        }
    }
}