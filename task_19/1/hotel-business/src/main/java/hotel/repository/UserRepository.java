package hotel.repository;

import hotel.exception.dao.DAOException;
import hotel.model.security.Role;
import hotel.model.security.User;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.dao.support.DataAccessUtils.uniqueResult;

@Repository
public class UserRepository extends BaseRepository<User, Integer> {
    public UserRepository() {
        setEntityClass(User.class);
    }

    public Optional<User> findById(Integer id) {
        return executeWithResult("findById",
                session -> Optional.ofNullable(session.get(User.class, id)),
                "id", id, "entity", "User"
        );
    }

    public List<User> findAll() {
        return executeWithResult("findAll",
                session -> session.createQuery("from User", User.class).list(),
                "entity", "User"
        );
    }

    public Integer save(User entity) {
        return executeWithResult("save",
                session -> {
                    Integer id = (Integer) session.save(entity);

                    if (entity.getRoles() != null && !entity.getRoles().isEmpty()) {
                        for (Role role : entity.getRoles()) {
                            if (role.getId() == null) {
                                session.save(role);
                            }
                        }
                    }

                    return id;
                },
                "username", entity.getUsername(),
                "email", entity.getEmail(),
                "roles", entity.getRoles() != null ? entity.getRoles().size() : 0
        );
    }

    public void update(User entity) {
        execute("update",
                session -> session.update(entity),
                "username", entity.getUsername(),
                "password", entity.getPassword()
        );
    }

    public void delete(User entity) {
        execute("delete",
                session -> {
                    Long userCount = (Long) session.createQuery(
                                    "select count(u) from User u where u.id == :userId")
                            .setParameter("userId", entity.getId())
                            .uniqueResult();

                    if (userCount > 0) {
                        throw new DAOException(
                                new IllegalStateException("User cannot be deleted"),
                                "Невозможно удалить юзера"
                        );
                    }

                    session.delete(entity);
                },
                "username", entity.getUsername());
    }

    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }

        return executeWithResult("findByUsername",
                session -> {
                    User user = session.createQuery(
                                    "SELECT u FROM User u " +
                                            "LEFT JOIN FETCH u.roles " +  // Принудительный FETCH
                                            "WHERE u.username = :username", User.class)
                            .setParameter("username", username)
                            .uniqueResult();

                    if (user != null) {
                        // Принудительно инициализируем коллекцию
                        Hibernate.initialize(user.getRoles());
                    }

                    return Optional.ofNullable(user);
                },
                "username", username
        );
    }
}
