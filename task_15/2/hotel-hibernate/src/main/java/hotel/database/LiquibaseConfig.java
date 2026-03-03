package hotel.database;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@PropertySource("classpath:database.properties")
public class LiquibaseConfig {
    private static final Logger log = LoggerFactory.getLogger(LiquibaseConfig.class);

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        String url = env.getProperty("db.url");
        String user = env.getProperty("db.user");
        String password = env.getProperty("db.password");

        log.info("Создание DataSource для БД: {}", url);

        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return DriverManager.getConnection(url, user, password);
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return DriverManager.getConnection(url, username, password);
            }

            @Override
            public java.io.PrintWriter getLogWriter() { return null; }
            @Override
            public void setLogWriter(java.io.PrintWriter out) {}
            @Override
            public void setLoginTimeout(int seconds) {}
            @Override
            public int getLoginTimeout() { return 0; }
            @Override
            public java.util.logging.Logger getParentLogger() { return null; }
            @Override
            public <T> T unwrap(Class<T> iface) { return null; }
            @Override
            public boolean isWrapperFor(Class<?> iface) { return false; }
        };
    }

    @Bean
    @DependsOn("dataSource")
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();

        log.info("Инициализация Liquibase...");

        liquibase.setChangeLog("db/changelog/db.changelog-master.xml");
        liquibase.setDataSource(dataSource);
        liquibase.setShouldRun(true);

        log.info("Liquibase настроен. ChangeLog: {}", liquibase.getChangeLog());

        return liquibase;
    }
}