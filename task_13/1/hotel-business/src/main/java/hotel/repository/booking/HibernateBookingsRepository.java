package hotel.repository.booking;

import hotel.annotation.Component;
import hotel.exception.booking.BookingNotFoundException;
import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.service.Services;
import hotel.repository.BaseRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class HibernateBookingsRepository extends BaseRepository<Bookings, Integer> implements BookingsRepository {
    private final static String FIND_ACTIVE_BOOKINGS_HQL = """
            SELECT DISTINCT b FROM Bookings b
            LEFT JOIN FETCH b.client c
            LEFT JOIN FETCH b.room r
            LEFT JOIN FETCH b.services s
            WHERE b.status = :status
            AND :date BETWEEN b.checkInDate AND b.checkOutDate
            ORDER BY b.checkInDate
            """;

    private final static String FIND_BY_ROOM_ID_HQL = """
            SELECT DISTINCT b FROM Bookings b
            LEFT JOIN FETCH b.client c
            LEFT JOIN FETCH b.room r
            LEFT JOIN FETCH b.services s
            WHERE b.room.id = :roomId
            ORDER BY b.checkInDate
            """;
    private final static String FIND_BY_CLIENT_ID_HQL = """
            SELECT DISTINCT b FROM Bookings b
            LEFT JOIN FETCH b.client c
            LEFT JOIN FETCH b.room r
            LEFT JOIN FETCH b.services s
            WHERE b.client.id = :clientId
            ORDER BY b.checkInDate DESC
            """;

    private final static String FIND_ACTIVE_BY_ROOM_ID_HQL = """
            SELECT b FROM Bookings b
            LEFT JOIN FETCH b.client c
            LEFT JOIN FETCH b.room r
            LEFT JOIN FETCH b.services s
            WHERE b.room.id = :roomId
            AND b.status = :status
            AND :date BETWEEN b.checkInDate AND b.checkOutDate
            """;

    private final static String GET_BOOKING_SERVICES_HQL = """
            SELECT s FROM Bookings b
            JOIN b.services s
            WHERE b.id = :bookingId
            ORDER BY s.name
            """;
    public HibernateBookingsRepository() {
        setEntityClass(Bookings.class);
    }

    @Override
    /// Найти активные брони по дате
    public List<Bookings> findActiveBookings(LocalDate date) {
        return getCurrentSession().
                createQuery(FIND_ACTIVE_BOOKINGS_HQL, Bookings.class).
                setParameter("status", BookingStatus.CONFIRMED).
                setParameter("date", date).
                list();
    }

    /// Найти бронирования по статусу
    public List<Bookings> findByStatus(BookingStatus status) {
        String hql = "FROM Bookings b WHERE b.status = :status ORDER BY b.checkInDate";
        return getCurrentSession()
                .createQuery(hql, Bookings.class)
                .setParameter("status", status)
                .list();
    }

    @Override
    /// Найти бронирования по id комнаты
    public List<Bookings> findByRoomId(Integer roomId) {
        return getCurrentSession().
                createQuery(FIND_BY_ROOM_ID_HQL, Bookings.class).
                setParameter("roomId", roomId).
                list();
    }

    @Override
    /// Найти активные бронирования по id клиента
    public List<Bookings> findByClientId(Integer clientId) {
        return getCurrentSession().
                createQuery(FIND_BY_CLIENT_ID_HQL, Bookings.class).
                setParameter("clientId", clientId).
                list();
    }

    @Override
    /// Найти активные бронирования по id комнаты
    public List<Bookings> findActiveByRoomId(Integer roomId, LocalDate date) {
        return getCurrentSession().
                createQuery(FIND_ACTIVE_BY_ROOM_ID_HQL, Bookings.class).
                setParameter("roomId", roomId).
                setParameter("status", BookingStatus.CONFIRMED).
                setParameter("date", date).
                list();
    }

    @Override
    /// Добавляет услуги в бронь
    public Optional<Bookings> addBookingServices(Integer bookingId, List<Integer> serviceIds) {
        Optional<Bookings> booking = findById(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException(bookingId);
        }

        List<Services> currentServices = booking.get().getServices();
        if (currentServices == null) {
            currentServices = new java.util.ArrayList<>();
            booking.get().setServices(currentServices);
        }
        boolean changed = false;

        for (Integer serviceId : serviceIds) {
            Services service = getCurrentSession().get(Services.class, serviceId);
            if (service == null) {
                throw new RuntimeException("Service not found with id: " + serviceId);
            }

            if (!currentServices.contains(service)) {
                currentServices.add(service);
                changed = true;
            }
        }

        if (changed) {
            recalculateTotalPrice(booking.get());
            booking.get().setUpdatedAt(LocalDateTime.now());
            update(booking.get());
        }

        return booking;
    }

    @Override
    /// Удаляем услуги(у) из брони
    public Optional<Bookings> removeBookingServices(Integer bookingId, List<Integer> serviceIds) {
        Optional<Bookings> booking = findById(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException(bookingId);
        }
        List<Services> services = booking.get().getServices();

        boolean changed = services.removeIf(service -> serviceIds.contains(service.getId()));
        if (changed) {
            recalculateTotalPrice(booking.get());
            booking.get().setUpdatedAt(LocalDateTime.now());
            update(booking.get());
        }
        return booking;
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

    @Override
    /// Получаем все услуги бронирования
    public List<Services> getBookingServices(Integer bookingId) {
        return getCurrentSession().
                createQuery(GET_BOOKING_SERVICES_HQL, Services.class).
                setParameter("bookingId", bookingId).
                list();
    }

}
