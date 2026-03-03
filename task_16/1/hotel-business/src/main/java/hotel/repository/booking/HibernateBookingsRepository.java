package hotel.repository.booking;

import hotel.exception.HotelException;
import hotel.exception.dao.DAOException;
import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.service.Services;
import hotel.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class HibernateBookingsRepository extends BaseRepository<Bookings, Integer> implements BookingsRepository {

    public HibernateBookingsRepository() {
        setEntityClass(Bookings.class);
    }

    @Override
    public Optional<Bookings> findById(Integer id) {
        return executeWithResult("findById",
                session -> {
                    String hql = """
                        SELECT DISTINCT b FROM Bookings b
                        LEFT JOIN FETCH b.client
                        LEFT JOIN FETCH b.room
                        LEFT JOIN FETCH b.services
                        WHERE b.id = :id
                        """;

                    return session.createQuery(hql, Bookings.class)
                            .setParameter("id", id)
                            .uniqueResultOptional();
                },
                "id", id
        );
    }

    @Override
    public List<Bookings> findAll() {
        return executeWithResult("findAll",
                session -> {
                    String hql = """
                        SELECT DISTINCT b FROM Bookings b
                        LEFT JOIN FETCH b.client
                        LEFT JOIN FETCH b.room
                        LEFT JOIN FETCH b.services
                        ORDER BY b.checkInDate DESC
                        """;

                    return session.createQuery(hql, Bookings.class).list();
                }
        );
    }

    @Override
    public Integer save(Bookings entity) {
        return executeWithResult("save",
                session -> (Integer) session.save(entity),
                "clientId", entity.getClient() != null ? entity.getClient().getId() : null,
                "roomId", entity.getRoom() != null ? entity.getRoom().getId() : null,
                "checkIn", entity.getCheckInDate()
        );
    }

    @Override
    public void update(Bookings entity) {
        execute("update",
                session -> {
                    entity.setUpdatedAt(LocalDateTime.now());
                    session.update(entity);
                },
                "bookingId", entity.getId()
        );
    }

    @Override
    public void delete(Bookings entity) {
        execute("delete",
                session -> session.delete(entity),
                "bookingId", entity.getId()
        );
    }

    @Override
    public List<Bookings> findActiveBookings(LocalDate date) {
        return executeWithResult("findActiveBookings",
                session -> {
                    String hql = """
                        SELECT DISTINCT b FROM Bookings b
                        LEFT JOIN FETCH b.client
                        LEFT JOIN FETCH b.room
                        LEFT JOIN FETCH b.services
                        WHERE b.status = :status
                        AND :date BETWEEN b.checkInDate AND b.checkOutDate
                        ORDER BY b.checkInDate
                        """;

                    return session.createQuery(hql, Bookings.class)
                            .setParameter("status", BookingStatus.CONFIRMED)
                            .setParameter("date", date)
                            .list();
                },
                "date", date
        );
    }

    @Override
    public List<Bookings> findByRoomId(Integer roomId) {
        return executeWithResult("findByRoomId",
                session -> {
                    String hql = """
                        SELECT DISTINCT b FROM Bookings b
                        LEFT JOIN FETCH b.client
                        LEFT JOIN FETCH b.room
                        LEFT JOIN FETCH b.services
                        WHERE b.room.id = :roomId
                        ORDER BY b.checkInDate
                        """;

                    return session.createQuery(hql, Bookings.class)
                            .setParameter("roomId", roomId)
                            .list();
                },
                "roomId", roomId
        );
    }

    @Override
    public List<Bookings> findByClientId(Integer clientId) {
        return executeWithResult("findByClientId",
                session -> {
                    String hql = """
                        SELECT DISTINCT b FROM Bookings b
                        LEFT JOIN FETCH b.client
                        LEFT JOIN FETCH b.room
                        LEFT JOIN FETCH b.services
                        WHERE b.client.id = :clientId
                        ORDER BY b.checkInDate DESC
                        """;

                    return session.createQuery(hql, Bookings.class)
                            .setParameter("clientId", clientId)
                            .list();
                },
                "clientId", clientId
        );
    }

    @Override
    public List<Bookings> findActiveByRoomId(Integer roomId, LocalDate date) {
        return executeWithResult("findActiveByRoomId",
                session -> {
                    String hql = """
                        SELECT b FROM Bookings b
                        LEFT JOIN FETCH b.client
                        LEFT JOIN FETCH b.room
                        LEFT JOIN FETCH b.services
                        WHERE b.room.id = :roomId
                        AND b.status = :status
                        AND :date BETWEEN b.checkInDate AND b.checkOutDate
                        """;

                    return session.createQuery(hql, Bookings.class)
                            .setParameter("roomId", roomId)
                            .setParameter("status", BookingStatus.CONFIRMED)
                            .setParameter("date", date)
                            .list();
                },
                "roomId", roomId, "date", date
        );
    }

    @Override
    public Optional<Bookings> addBookingServices(Integer bookingId, List<Integer> serviceIds) {
        return executeWithResult("addBookingServices",
                session -> {
                    Bookings booking = session.get(Bookings.class, bookingId);
                    if (booking == null) {
                        throw HotelException.bookingNotFound(bookingId);
                    }

                    List<Services> currentServices =  new ArrayList<>(booking.getServices() == null?
                            new ArrayList<>():booking.getServices());
                    boolean changed = false;

                    for (Integer serviceId : serviceIds) {
                        Services service = session.get(Services.class, serviceId);
                        if (service == null) {
                            throw new DAOException(new IllegalArgumentException(),
                                    "Service not found with id: " + serviceId);
                        }

                        if (!currentServices.contains(service)) {
                            currentServices.add(service);
                            changed = true;
                        }
                    }

                    if (changed) {
                        booking.setServices(currentServices);
                        recalculateTotalPrice(booking);
                        booking.setUpdatedAt(LocalDateTime.now());
                        session.update(booking);
                    }

                    return Optional.of(booking);
                },
                "bookingId", bookingId, "serviceIds", serviceIds
        );
    }

    @Override
    public Optional<Bookings> removeBookingServices(Integer bookingId, List<Integer> serviceIds) {
        return executeWithResult("removeBookingServices",
                session -> {
                    Bookings booking = session.get(Bookings.class, bookingId);
                    if (booking == null) {
                        throw HotelException.bookingNotFound(bookingId);
                    }

                    List<Services> services = new ArrayList<>(booking.getServices() == null?
                            new ArrayList<>():booking.getServices());
                    boolean changed = services.removeIf(service -> serviceIds.contains(service.getId()));

                    if (changed) {
                        booking.setServices(services);
                        recalculateTotalPrice(booking);
                        booking.setUpdatedAt(LocalDateTime.now());
                        session.update(booking);
                    }

                    return Optional.of(booking);
                },
                "bookingId", bookingId, "serviceIds", serviceIds
        );
    }

    @Override
    public List<Services> getBookingServices(Integer bookingId) {
        return executeWithResult("getBookingServices",
                session -> {
                    String hql = """
                        SELECT s FROM Bookings b
                        JOIN b.services s
                        WHERE b.id = :bookingId
                        ORDER BY s.name
                        """;

                    return session.createQuery(hql, Services.class)
                            .setParameter("bookingId", bookingId)
                            .list();
                },
                "bookingId", bookingId
        );
    }

    private void recalculateTotalPrice(Bookings booking) {
        BigDecimal roomPrice = booking.getRoom().getPrice();
        long days = java.time.temporal.ChronoUnit.DAYS.between(
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );
        BigDecimal total = roomPrice.multiply(BigDecimal.valueOf(days));

        if (booking.getServices() != null) {
            for (Services service : booking.getServices()) {
                total = total.add(service.getPrice());
            }
        }
        booking.setTotalPrice(total);
    }
}