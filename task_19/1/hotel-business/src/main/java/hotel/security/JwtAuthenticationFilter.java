package hotel.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("========== JWT FILTER STARTED ==========");
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Request method: {}", request.getMethod());
        log.info("Context path: {}", request.getContextPath());
        log.info("Servlet path: {}", request.getServletPath());

        log.info("--- ALL HEADERS ---");
        Collections.list(request.getHeaderNames()).forEach(headerName -> {
            log.info("Header '{}': '{}'", headerName, request.getHeader(headerName));
        });

        // Проверяем, нужно ли пропустить фильтр
        if (shouldNotFilter(request)) {
            log.info("Skipping JWT filter for public path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Извлекаем токен из заголовка
            String token = extractToken(request);
            log.info("Token extracted: {}", token != null ? "YES" : "NO");

            if (token != null) {
                log.info("Token length: {}", token.length());
                log.info("Token preview: {}...", token.substring(0, Math.min(20, token.length())));

                log.info("Validating token...");
                boolean isValid = jwtService.validateToken(token);
                log.info("Token validation result: {}", isValid);

                if (isValid) {
                    // Извлечем имя пользователя
                    String username = jwtService.extractUsername(token);
                    log.info("Token valid for user: {}", username);

                    // Запросим подробную инфу о пользователе
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    log.info("UserDetails loaded: {}", userDetails.getUsername());
                    log.info("User authorities: {}", userDetails.getAuthorities());

                    // Создадим объект аутентификации
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );

                    // И установим этот объект в контекст безопасности, в текущий поток выполнения программы
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Authentication set in SecurityContext");

                    // Проверяем, что установилось
                    log.info("SecurityContext authentication: {}",
                            SecurityContextHolder.getContext().getAuthentication());
                } else {
                    log.warn("Token is invalid");
                }
            } else {
                log.warn("No token found in request");
                log.warn("Authorization header was: {}", request.getHeader("Authorization"));
            }

            log.info("Proceeding with filter chain...");
            // Передаем управление следующему фильтру в цепочке
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Error in JWT filter", e);
            filterChain.doFilter(request, response);
        }

        log.info("=====SecurityWebApplicationInitializer===== JWT FILTER FINISHED ==========");
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("Authorization header: '{}'", bearerToken);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}