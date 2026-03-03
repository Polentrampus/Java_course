package repository;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {
        "hotel.repository",
        "hotel.service"
})
@EnableTransactionManagement
@Profile("test")
public class TestHibernateConfig {

    @Bean
    @Primary
    public SessionFactory testSessionFactory() {
        System.out.println("=== Creating TEST SessionFactory with H2 ===");

        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration()
                .addAnnotatedClass(hotel.model.service.Services.class)
                .addAnnotatedClass(hotel.model.users.Person.class)
                .addAnnotatedClass(hotel.model.users.client.Client.class)
                .addAnnotatedClass(hotel.model.users.employee.Employee.class)
                .addAnnotatedClass(hotel.model.room.Room.class)
                .addAnnotatedClass(hotel.model.booking.Bookings.class);

        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        configuration.setProperty("hibernate.connection.username", "sa");
        configuration.setProperty("hibernate.connection.password", "");

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.current_session_context_class", "thread");

        return configuration.buildSessionFactory();
    }

    @Bean
    @Primary
    public hotel.TransactionManager testTransactionManager(SessionFactory sessionFactory) {
        return new hotel.TransactionManager(sessionFactory);
    }
}