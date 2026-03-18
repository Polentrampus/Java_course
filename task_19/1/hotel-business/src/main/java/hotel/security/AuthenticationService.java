package hotel.security;

import hotel.dto.UserDto;
import hotel.model.security.Role;
import hotel.model.security.User;
import hotel.model.users.Person;
import hotel.repository.RoleRepository;
import hotel.repository.UserRepository;
import hotel.repository.person.PersonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager,
                                 JwtService jwtService, UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder,
                                 PersonRepository personRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
    }

    public JwtResponse authenticate(LoginRequest request) {
        log.info("=== AUTHENTICATION ATTEMPT ===");
        log.info("Username: {}", request.getUsername());
        log.info("Password provided: {}", request.getPassword() != null ? "YES" : "NO");
        try{
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("User loaded from DB:");
            log.info(" - Username: {}", user.getUsername());
            log.info(" - Roles collection: {}", user.getRoles());
            log.info(" - Roles size: {}", user.getRoles().size());
            log.info(" - Roles names: {}",
                    user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

            log.info("Generating); JWT token for user: {}", request.getUsername());
            String jwtToken = jwtService.generateToken(user.getUsername());
            log.info("JWT token generated successfully for user: {}", request.getUsername());

            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setToken(jwtToken);
            jwtResponse.setUsername(user.getUsername());
            jwtResponse.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()));

            log.info("Authentication process completed " +
                    "successfully for user: {}", request.getUsername());
            return jwtResponse;
        }catch (Exception e) {
            log.error("!!! AUTHENTICATION FAILED !!!", e);
            log.error("Exception type: {}", e.getClass().getName());
            log.error("Exception message: {}", e.getMessage());
            throw e;  // Пробрасываем дальше
        }
    }

    public UserDto register(RegisterRequest request) throws SQLException {
        log.info("=== REGISTRATION ATTEMPT ===");
        log.info("Username: {}", request.getUsername());
        log.info("Email: {}", request.getEmail());
        log.info("PersonId: {}", request.getPersonId());
        try{// Проверка существования пользователя
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new RuntimeException("Username already exists");
            }

            log.info("Creating new user with username: {}", request.getUsername());
            // Создание нового пользователя
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            log.info("User details set for username: {}", request.getUsername());

            if (request.getPersonId() != null) {
                Person person = personRepository.findById(request.getPersonId())
                        .orElseThrow(() -> new RuntimeException("Person not found"));
                user.setPerson(person);
            }

            log.info( "Assigning default role USER to new user: {}", request.getUsername());
            // Назначение роли по умолчанию
            Role userRole = roleRepository.findByName("ROLE_CLIENT")
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            log.info("Role found: {}", userRole.getName());
            log.info("Role ID: {}", userRole.getId());

            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);

            log.info("Roles set size before save: {}", user.getRoles().size());

            userRepository.save(user);

            User savedUser = userRepository.findByUsername(request.getUsername()).orElse(null);
            if (savedUser != null) {
                log.info("After save - roles size: {}", savedUser.getRoles().size());
            }
            return new UserDto(user);
        }catch (Exception e) {
            log.error("!!! REGISTRATION FAILED !!!", e);
            log.error("Exception type: {}", e.getClass().getName());
            log.error("Exception message: {}", e.getMessage());
            throw e;
        }
    }
}