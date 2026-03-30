package hotel.repository;

import hotel.exception.dao.DAOException;
import hotel.model.security.Role;
import hotel.model.security.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepository extends BaseRepository<Role, Integer>{
    public RoleRepository() {
        setEntityClass(Role.class);
    }

    public Optional<Role> findById(Integer id) {
        return executeWithResult("findById",
                session -> Optional.ofNullable(session.get(Role.class, id)),
                "id", id, "entity", "Role"
        );
    }

    public List<Role> findAll() {
        return executeWithResult("findAll",
                session -> session.createQuery("from Role", Role.class).list(),
                "entity", "Role"
        );
    }

    public Integer save(Role entity) {
        return executeWithResult("save",
                session -> (Integer) session.save(entity),
                "name", entity.getName(),
                "description", entity.getDescription(),
                "users", entity.getUsers()
        );
    }

    public void update(Role entity) {
        execute("update",
                session -> session.update(entity),
                "name", entity.getName(),
                "description", entity.getDescription(),
                "users", entity.getUsers()
        );
    }

    public void delete(Role entity) {
        execute("delete",
                session -> {
                    Long roleCount = (Long) session.createQuery(
                                    "select count(u) from Role u where u.id == :roleId")
                            .setParameter("roleId", entity.getId())
                            .uniqueResult();

                    if (roleCount > 0) {
                        throw new DAOException(
                                new IllegalStateException("Role cannot be deleted"),
                                "Невозможно удалить роль"
                        );
                    }

                    session.delete(entity);
                },
                "name", entity.getName());
    }
    public Optional<Role> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }

        return executeWithResult("findByName",
                session -> session.createQuery(
                                "FROM Role r WHERE r.name = :name", Role.class)
                        .setParameter("name", name)
                        .uniqueResultOptional(),
                "name", name, "entity", "Role"
        );
    }
}
