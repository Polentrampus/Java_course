package hotel.security;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * Входная точка для всей безопасности. Регистрирует в серулет контейнере фильтр DelegatingFilterProxy,
 * который перенаправляет запросы к Spring Security.
 * Этот класс автоматически обнаруживается Spring и используется для настройки безопасности.
 */
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
}
