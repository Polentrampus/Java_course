package hotel.service;

import hotel.util.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionContext {
    private static final ThreadLocal<Session> currentSession = new ThreadLocal<>();
    private static Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    public static void setSession(Session session) {
        currentSession.set(session);
    }

    public static Session getCurrentSession() {
        Session session = currentSession.get();
        if (session == null) {
            throw new IllegalStateException("No session in context");
        }
        return session;
    }

    public static void clear() {
        currentSession.remove();
    }
}