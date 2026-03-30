package hotel.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement //поддерживает транзакции
@PropertySource("classpath:database.properties")
@ComponentScan({
        "hotel.model",
        "hotel.repository",
        "hotel.service",
        "hotel.controller"
})
public class DatabaseConfig {
    private String url;
    private String username;
    private String password;
    private String driver;

    @PostConstruct
    public void init() {
        url = getEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://localhost:5432/hotel_management");
        username = getEnv("SPRING_DATASOURCE_USERNAME", "postgres");
        password = getEnv("SPRING_DATASOURCE_PASSWORD", "8642");
        driver = "org.postgresql.Driver";


        System.out.println("=== DATABASE CONFIGURATION ===");
        System.out.println("URL: " + url);
        System.out.println("Username: " + username);
        System.out.println("Driver: " + driver);
        System.out.println("==============================");
    }

    private String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return value != null ? value : defaultValue;
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);
        return new HikariDataSource(config);
    }
}
