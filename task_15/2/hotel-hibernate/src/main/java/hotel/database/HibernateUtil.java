package hotel.database;

import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.service.Services;
import hotel.model.users.Person;
import hotel.model.users.client.Client;
import hotel.model.users.employee.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Properties;

@Component
@DependsOn("liquibase")
@PropertySource("classpath:database.properties")
public class HibernateUtil {
    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    public HibernateUtil() {
    }

    @Value("${db.driver}")
    private String driver;

    @Value("${db.url}")
    private String url;

    @Value("${db.user}")
    private String user;

    @Value("${db.password}")
    private String password;

    @Value("${db.dialect}")
    private String dialect;

    private SessionFactory sessionFactory;

    public void init() {
        try {
            Configuration configuration = new Configuration();

            configuration.addAnnotatedClass(Person.class);
            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Employee.class);
            configuration.addAnnotatedClass(Room.class);
            configuration.addAnnotatedClass(Services.class);
            configuration.addAnnotatedClass(Bookings.class);

            Properties settings = new Properties();

            settings.put(Environment.DRIVER, driver);
            settings.put(Environment.URL, url);
            settings.put(Environment.USER, user);
            settings.put(Environment.PASS, password);

            settings.put(Environment.DIALECT, dialect);
            settings.put(Environment.SHOW_SQL, "true");
            settings.put(Environment.FORMAT_SQL, "true");
            settings.put(Environment.HBM2DDL_AUTO, "validate");
            settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

            configuration.setProperties(settings);
            sessionFactory = configuration.buildSessionFactory();

            log.info("Hibernate SessionFactory успешно создана");

        } catch (Throwable ex) {
            log.error("Ошибка при создании SessionFactory: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to create SessionFactory", ex);
        }
    }

    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @PreDestroy
    public void shutDown() {
        if(sessionFactory != null && !sessionFactory.isClosed()){
            sessionFactory.close();
            log.info("SessionFactory закрыта");
        }
    }
}