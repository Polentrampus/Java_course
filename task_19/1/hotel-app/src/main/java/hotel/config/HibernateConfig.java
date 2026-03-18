package hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.DependsOn;
import javax.sql.DataSource;
import java.util.Properties;
import org.springframework.context.annotation.Import;

@Configuration
@EnableTransactionManagement
@Import(LiquibaseConfig.class)
public class HibernateConfig {

    @Bean
    @DependsOn("liquibase")
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("hotel.model");

        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        hibernateProperties.setProperty("hibernate.show_sql", "true");
        hibernateProperties.setProperty("hibernate.format_sql", "true");
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "validate");

        hibernateProperties.setProperty("javax.persistence.validation.mode", "none");
        hibernateProperties.setProperty("hibernate.validator.autoregister_listeners", "false");

        sessionFactory.setHibernateProperties(hibernateProperties);

        return sessionFactory;
    }
}