package hotel.config;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:database.properties")
public class LiquibaseConfig {
    private static final Logger log = LoggerFactory.getLogger(LiquibaseConfig.class);
    @Autowired
    private DataSource  dataSource;

    @Bean
    @DependsOn("dataSource")
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();

        log.info("Инициализация Liquibase...");

        liquibase.setChangeLog("db/changelog/db.changelog-master.xml");
        liquibase.setDataSource(dataSource);
        liquibase.setShouldRun(true);

        log.info("Liquibase настроен. ChangeLog: {}", liquibase.getChangeLog());

        return liquibase;
    }
}