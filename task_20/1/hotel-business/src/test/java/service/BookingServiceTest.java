package service;

import config.TestHibernateConfig;
import hotel.dto.CreateBookingRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.booking.BookingNotFoundException;
import hotel.exception.dao.DAOException;
import hotel.exception.room.RoomNotFoundException;
import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.room.RoomType;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.client.ClientRepository;
import hotel.repository.room.RoomRepository;
import hotel.service.AdvancedBookingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // интеграция со мокито
@ContextConfiguration(classes = TestHibernateConfig.class)
@Transactional
@ActiveProfiles("test")
public class BookingServiceTest {
    @Mock
    private BookingsRepository bookingsRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private AdvancedBookingService bookingService;
    private Bookings testBookings;
    private Client testClient;
    private Room testRoom;
    private Services testService1;
    private Services testService2;

    @BeforeEach
    public void setUp() {
        testClient = new Client();
        testClient.setId(1);
        testClient.setName("testClient");
        testClient.setPatronymic("patronymic");
        testClient.setSurname("surname");
        testClient.setNotes("notes");

        testRoom = new Room();
        testRoom.setNumber(101);
        testRoom.setCapacity(2);
        testRoom.setPrice(BigDecimal.valueOf(2000));
        testRoom.setType(RoomType.STANDARD);

        testService1 = new Services();
        testService1.setId(1);
        testService1.setName("testService1");
        testService1.setPrice(BigDecimal.valueOf(3000));
        testService1.setDescription("testServiceDescriptor1");

        testService2 = new Services();
        testService2.setId(1);
        testService2.setName("testService2");
        testService2.setPrice(BigDecimal.valueOf(800));
        testService2.setDescription("testServiceDescriptor2");

        testBookings = new Bookings();
        testBookings.setId(1);
        testBookings.setRoom(testRoom);
        testBookings.setClient(testClient);
        testBookings.setCheckInDate(LocalDate.now());
        testBookings.setCheckOutDate(LocalDate.now().plusDays(3));
        testBookings.setStatus(BookingStatus.CONFIRMED);
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
    void deleteBookingById() throws SQLException {
        //arrange
        when(bookingsRepository.findById(1)).thenReturn(Optional.of(testBookings));
        doNothing().when(bookingsRepository).delete(testBookings);

        //act
        bookingService.deleteBookingById(1);
        //assert
        verify(bookingsRepository).findById(1);
        verify(bookingsRepository).delete(testBookings);
    }

    @Test
    void deleteBookingById_ShouldThrowBookingNotFoundException() throws SQLException {
        when(bookingsRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.deleteBookingById(999));

        verify(bookingsRepository).findById(999);
        verify(bookingsRepository, never()).delete(any());
    }

    @Test
    void deleteBookingById_ShouldThrowDAOException() throws SQLException {
        when(bookingsRepository.findById(1)).thenReturn(Optional.of(testBookings));
        doThrow(new RuntimeException("Database error")).when(bookingsRepository).delete(testBookings);

        assertThrows(HotelException.class, () -> bookingService.deleteBookingById(1));
    }

    @Test
    void updateBooking() throws SQLException {
        when(bookingsRepository.findById(1)).thenReturn(Optional.of(testBookings));
        when(roomRepository.findById(testRoom.getId())).thenReturn(Optional.of(testRoom));
        when(clientRepository.findById(testClient.getId())).thenReturn(Optional.of(testClient));

        CreateBookingRequest request = new CreateBookingRequest(testBookings);
        Optional<Bookings> booking = bookingService.updateBooking(request, testBookings.getId());

        assertThat(booking).isPresent();
        assertThat(booking.get().getUpdatedAt()).isEqualTo(testBookings.getUpdatedAt());
    }

    @Test
    void updateBooking_ShouldBookingNotFound() throws SQLException {
        CreateBookingRequest request = new CreateBookingRequest(testBookings);

        RuntimeException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateBooking(request, 999));

        verify(bookingsRepository).findById(999);
    }

    @Test
    void getAllBookings() {
        when(bookingsRepository.findAll()).thenReturn(Collections.singletonList(testBookings));
        List<Bookings> bookings = bookingService.getAllBookings();
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getAllBookings_ShouldReturnException() {
        when(bookingsRepository.findAll()).thenThrow(RuntimeException.class);
        RuntimeException exception = assertThrows(HotelException.class, () -> bookingService.getAllBookings());
    }

    @Test
    void getBookingById() throws SQLException {
        when(bookingsRepository.findById(1)).thenReturn(Optional.of(testBookings));
        Optional<Bookings> result = bookingService.getBookingById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        verify(bookingsRepository).findById(1);
    }

    @Test
    void getBookingById_ShouldReturnNotFound() throws SQLException {
        when(bookingsRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Bookings> result = bookingService.getBookingById(999);

        assertThat(result).isEmpty();
        verify(bookingsRepository).findById(999);
    }
    @Test
    void findActiveBookings() {
        LocalDate date = LocalDate.now();
        when(bookingsRepository.findActiveBookings(date)).thenReturn(List.of(testBookings));

        List<Bookings> result = bookingService.findActiveBookings(date);

        assertThat(result).hasSize(1);
        verify(bookingsRepository).findActiveBookings(date);
    }

    @Test
    void findByRoomId() {
        when(bookingsRepository.findByRoomId(101)).thenReturn(List.of(testBookings));

        List<Bookings> result = bookingService.findByRoomId(101);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRoom().getId()).isEqualTo(101);
        verify(bookingsRepository).findByRoomId(101);
    }


    @Test
    void findByClientId() {
        when(bookingsRepository.findByClientId(1)).thenReturn(List.of(testBookings));

        List<Bookings> result = bookingService.findByClientId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClient().getId()).isEqualTo(1);
        verify(bookingsRepository).findByClientId(1);
    }

    @Test
    void findActiveByRoomId() {
        LocalDate date = LocalDate.now();
        when(bookingsRepository.findActiveByRoomId(101, date))
                .thenReturn(List.of(testBookings));
        List<Bookings> result = bookingService.findActiveByRoomId(101, date);

        assertThat(result).hasSize(1);
        verify(bookingsRepository).findActiveByRoomId(101, date);
    }

    @Test
    void findActiveByRoomId_ShouldReturnThrowEmptyList() {
        LocalDate date = LocalDate.now();
        List<Bookings> result = bookingService.findActiveByRoomId(101, date);

        assertThat(result).hasSize(0);
        verify(bookingsRepository).findActiveByRoomId(101, date);
    }

    @Test
    void findActiveByClientId() {
        when(bookingsRepository.findActiveBookings(LocalDate.now())).thenReturn(Collections.singletonList(testBookings));

        List<Bookings> bookings = bookingService.findActiveByClientId(1);

        assertThat(bookings).isNotEmpty();
    }

    @Test
    void findActiveByClientId_ShouldReturnEmptyList() {
        when(bookingsRepository.findActiveBookings(LocalDate.now())).thenReturn(Collections.singletonList(testBookings));

        List<Bookings> bookings = bookingService.findActiveByClientId(999);

        assertThat(bookings).isEmpty();
    }

    @Test
    void createBooking() throws SQLException {
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(roomRepository.findById(101)).thenReturn(Optional.of(testRoom));
        when(bookingsRepository.save(any(Bookings.class)))
                .thenReturn(1);
        when(bookingsRepository.findById(1))
                .thenReturn(Optional.of(testBookings));

        CreateBookingRequest request = new CreateBookingRequest(testBookings);
        Optional<Bookings> bookings = bookingService.createBooking(request);

        assertThat(bookings).isPresent();
        assertThat(bookings.get().getId()).isEqualTo(testBookings.getId());
    }
    @Test
    void createBooking_ShouldThrowClientNotFound() throws SQLException {
        when(clientRepository.findById(999)).thenReturn(Optional.empty());

        CreateBookingRequest request = new CreateBookingRequest(testBookings);
        request.setClientId(999);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(HotelException.class);

        verify(clientRepository).findById(999);
        verify(roomRepository, never()).findById(any());
        verify(bookingsRepository, never()).save(any());
    }
}
