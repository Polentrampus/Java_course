package hotel.repository.room;

import hotel.exception.dao.DAOException;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.repository.BaseRepository;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HibernateRoomRepository extends BaseRepository<Room, Integer> implements RoomRepository {

    public HibernateRoomRepository() {
        setEntityClass(Room.class);
    }

    @Override
    public Optional<Room> findById(Integer id) {
        return executeWithResult("findById",
                session -> Optional.ofNullable(session.get(Room.class, id)),
                "id", id, "entity", "Room"
        );
    }

    @Override
    public List<Room> findAll() {
        return executeWithResult("findAll",
                session -> session.createQuery("from Room", Room.class).list(),
                "entity", "Room"
        );
    }

    @Override
    public Integer save(Room entity) {
        return executeWithResult("save",
                session -> (Integer) session.save(entity),
                "roomNumber", entity.getNumber(),
                "roomCategory", entity.getCategory(),
                "price", entity.getPrice()
        );
    }

    @Override
    public void update(Room entity) {
        execute("update",
                session -> session.update(entity),
                "roomNumber", entity.getNumber(),
                "roomStatus", entity.getStatus()
        );
    }

    @Override
    public void delete(Room entity) {
        execute("delete",
                session -> {
                    Long bookingCount = (Long) session.createQuery(
                                    "select count(b) from Bookings b where b.room.id = :roomId")
                            .setParameter("roomId", entity.getNumber())
                            .uniqueResult();

                    if (bookingCount > 0) {
                        throw new DAOException(
                                new IllegalStateException("Room has bookings"),
                                "Невозможно удалить комнату с существующими бронированиями"
                                                );
                    }

                    session.delete(entity);
                },
                "roomNumber", entity.getNumber(),
                "bookingCount", "checked"
        );
    }

    @Override
    public List<Room> listAvailableRooms(RoomFilter filter) {
        return executeWithResult("listAvailableRooms",
                session -> {
                    String hql = "FROM Room r WHERE r.status = :status";
                    if (filter != null) {
                        // Можно добавить сортировку в HQL
                        hql += " ORDER BY " + getOrderByClause(filter);
                    }

                    Query<Room> query = session.createQuery(hql, Room.class)
                            .setParameter("status", RoomStatus.AVAILABLE);

                    return query.list();
                },
                "filter", filter != null ? filter.name() : "none"
        );
    }

    @Override
    public List<Room> sortRooms(RoomFilter filter) {
        return executeWithResult("sortRooms",
                session -> {
                    String hql = "FROM Room r";
                    if (filter != null) {
                        hql += " ORDER BY " + getOrderByClause(filter);
                    }

                    return session.createQuery(hql, Room.class).list();
                },
                "filter", filter != null ? filter.name() : "none"
        );
    }

    @Override
    public void requestListRoomAndPrice(RoomFilter filter) {
        execute("requestListRoomAndPrice",
                session -> {
                    List<Room> rooms = sortRooms(filter);
                    System.out.println("Номер комнаты/цена");
                    for (Room room : rooms) {
                        System.out.println(room.getNumber() + " " + room.getPrice());
                    }
                },
                "filter", filter != null ? filter.name() : "none"
        );
    }

    private String getOrderByClause(RoomFilter filter) {
        return switch (filter) {
            case CAPACITY -> "r.capacity";
            case TYPE -> "r.type";
            case CATEGORY -> "r.category";
            case PRICE -> "r.price";
            case ID -> "r.number";
            default -> "r.number";
        };
    }
}