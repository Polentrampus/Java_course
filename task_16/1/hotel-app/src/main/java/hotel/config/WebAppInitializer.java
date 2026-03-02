package hotel.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import jakarta.servlet.ServletException;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?> [] getRootConfigClasses() {
        return new Class[]{DatabaseConfig.class,
                HibernateConfig.class,
                TransactionConfig.class,
                AppConfig.class };
    }

    @Override
    protected Class<?> [] getServletConfigClasses() {
        return new Class[] {WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }
}
