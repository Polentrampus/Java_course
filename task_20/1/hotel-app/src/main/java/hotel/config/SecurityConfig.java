package hotel.config;

import hotel.exception.security.CustomAccessDeniedHandler;
import hotel.exception.security.CustomAuthenticationEntryPoint;
import hotel.security.JwtAuthenticationFilter;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //включили веб-безопасность (создает основной фильтр springSecurityFilterChain)
@EnableMethodSecurity // Для проверки прав с помощью @PreAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SecurityConfig.class);

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthFilter,
            UserDetailsService userDetailsService,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        log.info("SecurityConfig initialized with JwtAuthenticationFilter: {}", jwtAuthFilter != null);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("=== CONFIGURING SECURITY FILTER CHAIN ===");
        log.info("JwtAuthenticationFilter instance: {}", jwtAuthFilter);

        http
                .csrf(csrf -> csrf.disable()) // Отключаем, т.к. актуально для сессий, а у нас stateless
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(authz -> {
                    log.info("Configuring authorization rules...");
                    authz
                            .requestMatchers("/auth/**").permitAll()
                            .requestMatchers("/rooms/public/**").permitAll()
                            .requestMatchers("/rooms/createRoom").hasRole("ADMIN")
                            .requestMatchers("/rooms/{id}/status").hasRole("EMPLOYEE")
                            .requestMatchers("/rooms/{id}/price").hasRole("EMPLOYEE")

                            .requestMatchers("/services/**").authenticated()
                            .requestMatchers("/clients/**").authenticated()
                            .requestMatchers("/employees/**").authenticated()
                            .requestMatchers("/bookings/**").authenticated()

                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Явно указываем что без сессий, т.к. используем JWT
                )
                .authenticationProvider(authenticationProvider()) // Регистрируем провайдера, для проверки лог\пароля
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Встраиваем свой фильтр
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler) //добавляем кастомные обработчики ошибок
                        // для AccessDeniedException, которые предварительно настроили
                );

        log.info("JWT filter added before UsernamePasswordAuthenticationFilter");
        log.info("Security filter chain built successfully");

        return http.build();
    }

    /**
     * Настраивает провайдер аутентификации, который использует наш CustomUserDetailsService и BCrypt для паролей
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Менеджер аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * BCrypt - стандарт де-факто для паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);  // 10 - strength factor
    }
}
