package repository;

import hotel.database.TransactionManager;
import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.room.RoomType;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.repository.booking.HibernateBookingsRepository;
import hotel.repository.client.HibernateClientRepository;
import hotel.repository.room.HibernateRoomRepository;
import hotel.repository.service.ServicesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestHibernateConfig.class)
@ActiveProfiles("test")
public class HibernateBookingRepositoryTest {
    @Autowired
    private HibernateBookingsRepository bookingsRepository;

    @Autowired
    private HibernateClientRepository clientRepository;

    @Autowired
    private HibernateRoomRepository roomRepository;

    @Autowired
    private ServicesRepository servicesRepository;

    @Autowired
    private TransactionManager transactionManager;

    private Client testClient;
    private Room testRoom;
    private Services testService1;
    private Services testService2;
    private Bookings testBookings;

    @BeforeEach
    public void setUp() {
        transactionManager.executeInTransaction(() -> {
            bookingsRepository.findAll().forEach(bookings -> {
                bookingsRepository.delete(bookings);
            });
            clientRepository.findAll().forEach(clients -> {
                clientRepository.delete(clients);
            });
            roomRepository.findAll().forEach(rooms -> {
                roomRepository.delete(rooms);
            });
            servicesRepository.findAll().forEach(services -> {
                servicesRepository.delete(services);
            });
            return null;
        });
        testClient = new Client();
        testClient.setName("testClient");
        testClient.setPatronymic("patronymic");
        testClient.setSurname("surname");
        testClient.setNotes("notes");

        transactionManager.executeInTransaction(() ->
                clientRepository.save(testClient)
        );

        testRoom = new Room();
        testRoom.setNumber(101);
        testRoom.setCapacity(2);
        testRoom.setPrice(BigDecimal.valueOf(2000));
        testRoom.setType(RoomType.STANDARD);

        transactionManager.executeInTransaction(() ->
                roomRepository.save(testRoom)
        );

        testService1 = new Services();
        testService1.setName("testService1");
        testService1.setPrice(BigDecimal.valueOf(3000));
        testService1.setDescription("testServiceDescriptor1");
        transactionManager.executeInTransaction(() ->
                servicesRepository.save(testService1)
        );

        testService2 = new Services();
        testService2.setName("testService2");
        testService2.setPrice(BigDecimal.valueOf(800));
        testService2.setDescription("testServiceDescriptor2");
        transactionManager.executeInTransaction(() ->
                servicesRepository.save(testService2)
        );

        testBookings = new Bookings();
        testBookings.setRoom(testRoom);
        testBookings.setClient(testClient);
        testBookings.setCheckInDate(LocalDate.now());
        testBookings.setCheckOutDate(LocalDate.now().plusDays(3));
        testBookings.setServices(List.of(testService1, testService2));

        long days = ChronoUnit.DAYS.between(
                testBookings.getCheckInDate(),
                testBookings.getCheckOutDate()
        );
        BigDecimal sum = BigDecimal.valueOf(days);
        sum = sum.multiply(testRoom.getPrice());
        BigDecimal servicesPrice = BigDecimal.ZERO;
        List<Services> services = List.of(testService1, testService2);
        for (Services service : services) {
            servicesPrice = servicesPrice.add(service.getPrice());
        }
        sum = sum.add(servicesPrice);

        testBookings.setTotalPrice(sum);
    }

    @Test
    @DisplayName("Тест сохранения нового бронирования")
    void save_ShouldPersistBooking() {
        Integer id = transactionManager.executeInTransaction(() ->
                bookingsRepository.save(testBookings)
        );

        assertThat(id).isNotNull();

        Optional<Bookings> found = transactionManager.executeInTransaction(() ->
                bookingsRepository.findById(id)
        );

        assertThat(found).isPresent();
        assertThat(found.get().getClient().getId()).isEqualTo(testClient.getId());
        assertThat(found.get().getRoom().getId()).isEqualTo(testRoom.getId());
        assertThat(found.get().getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Тест поиска активных бронирований по дате")
    void findActiveBookings_ShouldReturnActiveBookingsForDate() {
        save_ShouldPersistBooking();
        LocalDate testDate = LocalDate.now().plusDays(2);

        List<Bookings> activeBookings = transactionManager.executeInTransaction(() ->
                bookingsRepository.findActiveBookings(testDate)
        );

        assertThat(activeBookings).isNotNull();
        assertThat(activeBookings.get(0).getStatus()).isEqualTo(BookingStatus.CONFIRMED);

        Bookings booking = activeBookings.get(0);
        assertThat(testDate).isAfterOrEqualTo(booking.getCheckInDate())
                .isBeforeOrEqualTo(booking.getCheckOutDate());
    }

    @Test
    @DisplayName("Тест поиска по статусу")
    void findByStatus_ShouldReturnBookingsWithSpecifiedStatus() {
        save_ShouldPersistBooking();
        List<Bookings> confirmedBookings = transactionManager.executeInTransaction(() ->
                bookingsRepository.findByStatus(BookingStatus.CONFIRMED)
        );

        assertThat(confirmedBookings).isNotNull();
        assertThat(confirmedBookings)
                .allMatch(booking -> booking.getStatus() == BookingStatus.CONFIRMED);

        List<Bookings> pendingBookings = transactionManager.executeInTransaction(() ->
                bookingsRepository.findByStatus(BookingStatus.CANCELLED)
        );

        assertThat(pendingBookings).isEmpty();
    }

    @Test
    @DisplayName("Тест поиска по ID комнаты")
    void findByRoomId_ShouldReturnBookingsForSpecificRoom() {
        save_ShouldPersistBooking();
        List<Bookings> roomBookings = transactionManager.executeInTransaction(() ->
                bookingsRepository.findByRoomId(testRoom.getId())
        );

        assertThat(roomBookings).isNotEmpty();
        assertThat(roomBookings)
                .allMatch(booking -> booking.getRoom().getId().equals(testRoom.getId()));
    }

    @Test
    @DisplayName("Тест поиска по ID клиента")
    void findByClientId_ShouldReturnClientBookings() {
        save_ShouldPersistBooking();
        List<Bookings> clientBookings = transactionManager.executeInTransaction(() ->
                bookingsRepository.findByClientId(testClient.getId())
        );

        assertThat(clientBookings).isNotEmpty();
        assertThat(clientBookings)
                .allMatch(booking -> booking.getClient().getId().equals(testClient.getId()));

        if (clientBookings.size() > 1) {
            assertThat(clientBookings.get(0).getCheckInDate())
                    .isAfterOrEqualTo(clientBookings.get(1).getCheckInDate());
        }
    }

    @Test
    @DisplayName("Тест поиска активных бронирований по комнате")
    void findActiveByRoomId_ShouldReturnActiveBookingsForRoomOnDate() {
        save_ShouldPersistBooking();
        LocalDate testDate = LocalDate.now().plusDays(2);

        List<Bookings> activeRoomBookings = transactionManager.executeInTransaction(() ->
                bookingsRepository.findActiveByRoomId(testRoom.getId(), testDate)
        );

        assertThat(activeRoomBookings).isNotEmpty();

        assertThat(activeRoomBookings)
                .allMatch(booking -> booking.getRoom().getId().equals(testRoom.getId()));

        assertThat(activeRoomBookings)
                .allMatch(booking -> booking.getStatus() == BookingStatus.CONFIRMED);

        for (Bookings booking : activeRoomBookings) {
            assertThat(testDate)
                    .isAfterOrEqualTo(booking.getCheckInDate())
                    .isBeforeOrEqualTo(booking.getCheckOutDate());
        }
    }

    @Test
    @DisplayName("Тест добавления услуг в бронирование")
    void addBookingServices_ShouldAddServicesAndRecalculatePrice() {
        Bookings bookingWithoutServices = transactionManager.executeInTransaction(() -> {
            Bookings newBooking = new Bookings();
            newBooking.setClient(testClient);
            newBooking.setRoom(testRoom);
            newBooking.setCheckInDate(LocalDate.now().plusDays(20));
            newBooking.setCheckOutDate(LocalDate.now().plusDays(25));
            newBooking.setStatus(BookingStatus.CONFIRMED);

            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    newBooking.getCheckInDate(),
                    newBooking.getCheckOutDate()
            );
            newBooking.setTotalPrice(testRoom.getPrice().multiply(BigDecimal.valueOf(days)));

            bookingsRepository.save(newBooking);
            return newBooking;
        });

        BigDecimal priceBefore = bookingWithoutServices.getTotalPrice();

        Optional<Bookings> updatedBooking = transactionManager.executeInTransaction(() ->
                bookingsRepository.addBookingServices(
                        bookingWithoutServices.getId(),
                        List.of(testService1.getId(), testService2.getId())
                )
        );

        assertThat(updatedBooking).isPresent();
        assertThat(updatedBooking.get().getServices()).hasSize(2);
        assertThat(updatedBooking.get().getServices())
                .extracting(Services::getName)
                .containsExactlyInAnyOrder("testService1", "testService2");

        BigDecimal expectedPrice = priceBefore
                .add(testService1.getPrice())
                .add(testService2.getPrice());

        assertThat(updatedBooking.get().getTotalPrice())
                .isEqualByComparingTo(expectedPrice);
    }

    @Test
    @DisplayName("Тест удаления услуг из бронирования")
    void removeBookingServices_ShouldRemoveServicesAndRecalculatePrice() {
        save_ShouldPersistBooking();
        BigDecimal priceBefore = testBookings.getTotalPrice();

        Optional<Bookings> updatedBooking = transactionManager.executeInTransaction(() ->
                bookingsRepository.removeBookingServices(
                        testBookings.getId(),
                        List.of(testService1.getId())
                )
        );

        assertThat(updatedBooking).isPresent();
        assertThat(updatedBooking.get().getServices()).hasSize(1);
        assertThat(updatedBooking.get().getServices().get(0).getName())
                .isEqualTo("testService2");

        BigDecimal expectedPrice = priceBefore.subtract(testService1.getPrice());
        assertThat(updatedBooking.get().getTotalPrice())
                .isEqualByComparingTo(expectedPrice);
    }

    @Test
    @DisplayName("Тест получения услуг бронирования")
    void getBookingServices_ShouldReturnAllServicesForBooking() {
        save_ShouldPersistBooking();

        List<Services> services = transactionManager.executeInTransaction(() ->
                bookingsRepository.getBookingServices(testBookings.getId())
        );

        assertThat(services).hasSize(2);
        assertThat(services)
                .extracting(Services::getName)
                .containsExactly("testService1", "testService2");
    }

    @Test
    @DisplayName("Тест обновления бронирования")
    void update_ShouldModifyBooking() {
        save_ShouldPersistBooking();

        transactionManager.executeInTransaction(() -> {
            Bookings bookingToUpdate = bookingsRepository.findById(testBookings.getId())
                    .orElseThrow();
            bookingToUpdate.setStatus(BookingStatus.CONFIRMED);
            bookingToUpdate.setUpdatedAt(LocalDateTime.now());
            bookingsRepository.update(bookingToUpdate);
            return null;
        });

        Optional<Bookings> updated = transactionManager.executeInTransaction(() ->
                bookingsRepository.findById(testBookings.getId())
        );

        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Тест удаления бронирования")
    void delete_ShouldRemoveBooking() {
        save_ShouldPersistBooking();

        transactionManager.executeInTransaction(() -> {
            bookingsRepository.delete(testBookings);
            return null;
        });

        Optional<Bookings> found = transactionManager.executeInTransaction(() ->
                bookingsRepository.findById(testBookings.getId())
        );

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Тест на исключение при добавлении услуг к несуществующему бронированию")
    void addBookingServices_ShouldThrowException_WhenBookingNotFound() {
        Integer nonExistentId = 99999;

        assertThrows(RuntimeException.class, () -> {
            transactionManager.executeInTransaction(() ->
                    bookingsRepository.addBookingServices(nonExistentId, List.of(1, 2))
            );
        });
    }
}
