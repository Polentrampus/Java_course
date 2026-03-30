package repository;

import config.TestHibernateConfig;
import hotel.exception.dao.DAOException;
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
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
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

    private Client testClient;
    private Room testRoom;
    private Services testService1;
    private Services testService2;
    private Bookings testBookings;

    @BeforeEach
    public void setUp() {
        clearAllData();

        testClient = new Client();
        testClient.setName("testClient");
        testClient.setPatronymic("patronymic");
        testClient.setSurname("surname");
        testClient.setNotes("notes");

        clientRepository.save(testClient);

        testRoom = new Room();
        testRoom.setNumber(101);
        testRoom.setCapacity(2);
        testRoom.setPrice(BigDecimal.valueOf(2000));
        testRoom.setType(RoomType.STANDARD);

        roomRepository.save(testRoom);

        testService1 = new Services();
        testService1.setName("testService1");
        testService1.setPrice(BigDecimal.valueOf(3000));
        testService1.setDescription("testServiceDescriptor1");

        servicesRepository.save(testService1);

        testService2 = new Services();
        testService2.setName("testService2");
        testService2.setPrice(BigDecimal.valueOf(800));
        testService2.setDescription("testServiceDescriptor2");

        servicesRepository.save(testService2);

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

    private void clearAllData() {
        try {
            bookingsRepository.findAll().forEach(booking -> {
                try {
                    bookingsRepository.delete(booking);
                } catch (Exception e) {
                }
            });

            servicesRepository.findAll().forEach(service -> {
                try {
                    servicesRepository.delete(service);
                } catch (Exception e) {
                }
            });

            clientRepository.findAll().forEach(client -> {
                try {
                    clientRepository.delete(client);
                } catch (Exception e) {
                }
            });

            roomRepository.findAll().forEach(room -> {
                try {
                    roomRepository.delete(room);
                } catch (Exception e) {
                }
            });
        } catch (Exception e) {
            System.out.println("Error clearing data: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Тест сохранения нового бронирования")
    void save_ShouldPersistBooking() {
        Integer id = bookingsRepository.save(testBookings);

        assertThat(id).isNotNull();

        Optional<Bookings> found = bookingsRepository.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().getClient().getId()).isEqualTo(testClient.getId());
        assertThat(found.get().getRoom().getId()).isEqualTo(testRoom.getId());
        assertThat(found.get().getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Тест поиска активных бронирований по дате")
    void findActiveBookings_ShouldReturnActiveBookingsForDate() {
        bookingsRepository.save(testBookings);
        LocalDate testDate = LocalDate.now().plusDays(2);

        List<Bookings> activeBookings = bookingsRepository.findActiveBookings(testDate);

        assertThat(activeBookings).isNotEmpty();
        assertThat(activeBookings.get(0).getStatus()).isEqualTo(BookingStatus.CONFIRMED);

        Bookings booking = activeBookings.get(0);
        assertThat(testDate).isAfterOrEqualTo(booking.getCheckInDate())
                .isBeforeOrEqualTo(booking.getCheckOutDate());
    }

    @Test
    @DisplayName("Тест поиска по ID комнаты")
    void findByRoomId_ShouldReturnBookingsForSpecificRoom() {
        bookingsRepository.save(testBookings);

        List<Bookings> roomBookings = bookingsRepository.findByRoomId(testRoom.getId());

        assertThat(roomBookings).isNotEmpty();
        assertThat(roomBookings)
                .allMatch(booking -> booking.getRoom().getId().equals(testRoom.getId()));
    }

    @Test
    @DisplayName("Тест поиска по ID клиента")
    void findByClientId_ShouldReturnClientBookings() {
        bookingsRepository.save(testBookings);

        List<Bookings> clientBookings = bookingsRepository.findByClientId(testClient.getId());

        assertThat(clientBookings).isNotEmpty();
        assertThat(clientBookings)
                .allMatch(booking -> booking.getClient().getId() == (testClient.getId()));
    }

    @Test
    @DisplayName("Тест поиска активных бронирований по комнате")
    void findActiveByRoomId_ShouldReturnActiveBookingsForRoomOnDate() {
        bookingsRepository.save(testBookings);
        LocalDate testDate = LocalDate.now().plusDays(2);

        List<Bookings> activeRoomBookings = bookingsRepository.findActiveByRoomId(
                testRoom.getId(), testDate);

        assertThat(activeRoomBookings).isNotEmpty();
        assertThat(activeRoomBookings)
                .allMatch(booking -> booking.getRoom().getId().equals(testRoom.getId()));
        assertThat(activeRoomBookings)
                .allMatch(booking -> booking.getStatus() == BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Тест добавления услуг в бронирование")
    void addBookingServices_ShouldAddServicesAndRecalculatePrice() {
        Bookings bookingWithoutServices = new Bookings();
        bookingWithoutServices.setClient(testClient);
        bookingWithoutServices.setRoom(testRoom);
        bookingWithoutServices.setCheckInDate(LocalDate.now().plusDays(20));
        bookingWithoutServices.setCheckOutDate(LocalDate.now().plusDays(25));
        bookingWithoutServices.setStatus(BookingStatus.CONFIRMED);

        long days = ChronoUnit.DAYS.between(
                bookingWithoutServices.getCheckInDate(),
                bookingWithoutServices.getCheckOutDate()
        );
        bookingWithoutServices.setTotalPrice(
                testRoom.getPrice().multiply(BigDecimal.valueOf(days))
        );

        Integer bookingId = bookingsRepository.save(bookingWithoutServices);
        Bookings savedBooking = bookingsRepository.findById(bookingId).orElseThrow();
        BigDecimal priceBefore = savedBooking.getTotalPrice();

        Optional<Bookings> updatedBooking = bookingsRepository.addBookingServices(
                bookingId,
                List.of(testService1.getId(), testService2.getId())
        );

        assertThat(updatedBooking).isPresent();
        assertThat(updatedBooking.get().getServices()).hasSize(2);

        BigDecimal expectedPrice = priceBefore
                .add(testService1.getPrice())
                .add(testService2.getPrice());

        assertThat(updatedBooking.get().getTotalPrice())
                .isEqualByComparingTo(expectedPrice);
    }

    @Test
    @DisplayName("Тест удаления услуг из бронирования")
    void removeBookingServices_ShouldRemoveServicesAndRecalculatePrice() {
        Integer bookingId = bookingsRepository.save(testBookings);
        Bookings savedBooking = bookingsRepository.findById(bookingId).orElseThrow();
        BigDecimal priceBefore = savedBooking.getTotalPrice();

        Optional<Bookings> updatedBooking = bookingsRepository.removeBookingServices(
                bookingId,
                List.of(testService1.getId())
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
        Integer bookingId = bookingsRepository.save(testBookings);

        List<Services> services = bookingsRepository.getBookingServices(bookingId);

        assertThat(services).hasSize(2);
        assertThat(services)
                .extracting(Services::getName)
                .containsExactlyInAnyOrder("testService1", "testService2");
    }

    @Test
    @DisplayName("Тест обновления бронирования")
    void update_ShouldModifyBooking() {
        Integer bookingId = bookingsRepository.save(testBookings);

        Bookings bookingToUpdate = bookingsRepository.findById(bookingId).orElseThrow();
        bookingToUpdate.setStatus(BookingStatus.CANCELLED);
        bookingToUpdate.setUpdatedAt(LocalDateTime.now());
        bookingsRepository.update(bookingToUpdate);

        Optional<Bookings> updated = bookingsRepository.findById(bookingId);

        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    @DisplayName("Тест удаления бронирования")
    void delete_ShouldRemoveBooking() {
        Integer bookingId = bookingsRepository.save(testBookings);

        Bookings bookingToDelete = bookingsRepository.findById(bookingId).orElseThrow();
        bookingsRepository.delete(bookingToDelete);

        Optional<Bookings> found = bookingsRepository.findById(bookingId);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Тест на исключение при добавлении услуг к несуществующему бронированию")
    void addBookingServices_ShouldThrowException_WhenBookingNotFound() {
        Integer nonExistentId = 99999;

        assertThrows(DAOException.class, () -> {
            bookingsRepository.addBookingServices(nonExistentId, List.of(1, 2));
        });
    }
}
