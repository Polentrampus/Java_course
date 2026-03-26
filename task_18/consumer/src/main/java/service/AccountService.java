package service;

import model.Account;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final SessionFactory sessionFactory;

    public AccountService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Account findAccountById(Session session, Long accountId) {
        return session.get(Account.class, accountId);
    }
}
