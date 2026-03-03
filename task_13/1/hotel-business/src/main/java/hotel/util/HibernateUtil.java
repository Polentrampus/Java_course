package hotel.util;

import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.service.Services;
import hotel.model.users.Person;
import hotel.model.users.client.Client;
import hotel.model.users.employee.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
    private static SessionFactory sessionFactory = null;
    private static Session session = null;
    private static Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    static{
        try {
            sessionFactory = new Configuration().
                    addAnnotatedClass(Person.class).
                    addAnnotatedClass(Client.class).
                    addAnnotatedClass(Employee.class).
                    addAnnotatedClass(Room.class).
                    addAnnotatedClass(Services.class).
                    addAnnotatedClass(Bookings.class).
                    configure().
                    buildSessionFactory();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    public static Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    public static void shutDown() {
        if(session != null && session.isOpen()){
            sessionFactory.close();
            log.info("shutDouwn() - сессия закрыта");
        }
    }
}
