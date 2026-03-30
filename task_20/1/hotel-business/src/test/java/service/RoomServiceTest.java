package service;

import config.TestHibernateConfig;
import hotel.dto.CreateRoomRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.room.RoomAlreadyExistsException;
import hotel.exception.room.RoomNotFoundException;
import hotel.exception.room.RoomNotAvailableException;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.room.RoomRepository;
import hotel.service.ModifiableRoomService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestHibernateConfig.class)
@Transactional
@ActiveProfiles("test")
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingsRepository bookingsRepository;

    @InjectMocks
    private ModifiableRoomService roomService;

    private Room testRoom;
    private CreateRoomRequest createRoomRequest;

    @BeforeEach
    public void setUp() {
        testRoom = new Room();
        testRoom.setNumber(101);
        testRoom.setCategory(RoomCategory.ECONOMY);
        testRoom.setType(RoomType.STANDARD);
        testRoom.setStatus(RoomStatus.AVAILABLE);
        testRoom.setPrice(BigDecimal.valueOf(5000));
        testRoom.setCapacity(2);

        createRoomRequest = new CreateRoomRequest();
        createRoomRequest.setIdRoom(101);
        createRoomRequest.setRoomCategory(RoomCategory.ECONOMY);
        createRoomRequest.setRoomType(RoomType.STANDARD);
        createRoomRequest.setRoomStatus(RoomStatus.AVAILABLE);
        createRoomRequest.setPrice(BigDecimal.valueOf(5000));
        createRoomRequest.setCapacity(2);
    }

    @Test
    void addRoom() throws SQLException {
        when(roomRepository.findById(101)).thenReturn(Optional.empty());
        when(roomRepository.save(any(Room.class))).thenReturn(1);
        when(roomRepository.findById(1)).thenReturn(Optional.of(testRoom));

        Optional<Room> result = roomService.addRoom(createRoomRequest);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(101);
        assertThat(result.get().getNumber()).isEqualTo(101);
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void addRoom_ShouldThrowRoomAlreadyExistsException() throws SQLException {
        when(roomRepository.findById(101)).thenReturn(Optional.of(testRoom));

        assertThrows(RoomAlreadyExistsException.class, () -> roomService.addRoom(createRoomRequest));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void addRoom_WithNullPrice_ShouldThrowHotelException() {
        createRoomRequest.setPrice(null);

        assertThrows(HotelException.class, () -> roomService.addRoom(createRoomRequest));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void addRoom_WithNegativePrice_ShouldThrowHotelException() {
        createRoomRequest.setPrice(BigDecimal.valueOf(-1000));

        assertThrows(HotelException.class, () -> roomService.addRoom(createRoomRequest));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void addRoom_WithNullIdRoom_ShouldThrowHotelException() {
        createRoomRequest.setIdRoom(null);

        assertThrows(HotelException.class, () -> roomService.addRoom(createRoomRequest));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void addRoom_WithZeroCapacity_ShouldThrowHotelException() {
        createRoomRequest.setCapacity(0);

        assertThrows(HotelException.class, () -> roomService.addRoom(createRoomRequest));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void findById() throws SQLException {
        when(roomRepository.findById(101)).thenReturn(Optional.of(testRoom));

        Optional<Room> result = roomService.findById(101);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(101);
        verify(roomRepository).findById(101);
    }

    @Test
    void findById_ShouldReturnEmpty() throws SQLException {
        when(roomRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Room> result = roomService.findById(999);

        assertThat(result).isEmpty();
        verify(roomRepository).findById(999);
    }

    @Test
    void findById_WithNullId_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> roomService.findById(null));
        verify(roomRepository, never()).findById(any());
    }

    @Test
    void findAll() {
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        List<Room> rooms = roomService.findAll();

        assertThat(rooms).hasSize(1);
        assertThat(rooms.get(0).getId()).isEqualTo(101);
        verify(roomRepository).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(roomRepository.findAll()).thenReturn(Collections.emptyList());

        List<Room> rooms = roomService.findAll();

        assertThat(rooms).isEmpty();
        verify(roomRepository).findAll();
    }

    @Test
    void sortRooms() {
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        List<Room> rooms = roomService.sortRooms(RoomFilter.PRICE);

        assertThat(rooms).hasSize(1);
        verify(roomRepository).findAll();
    }

    @Test
    void sortRooms_WithNullFilter_ShouldUseDefault() {
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        List<Room> rooms = roomService.sortRooms(null);

        assertThat(rooms).hasSize(1);
        verify(roomRepository).findAll();
    }

    @Test
    void setStatusRoom() throws SQLException {
        when(roomRepository.findById(1)).thenReturn(Optional.of(testRoom));
        doNothing().when(roomRepository).update(any(Room.class));

        roomService.setStatusRoom(1, RoomStatus.OCCUPIED);

        assertThat(testRoom.getStatus()).isEqualTo(RoomStatus.OCCUPIED);
        verify(roomRepository).update(testRoom);
    }

    @Test
    void setStatusRoom_ShouldThrowRoomNotFoundException() throws SQLException {
        when(roomRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.setStatusRoom(999, RoomStatus.OCCUPIED));
        verify(roomRepository, never()).update(any());
    }

    @Test
    void setStatusRoom_WithNullStatus_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> roomService.setStatusRoom(1, null));
        verify(roomRepository, never()).findById(any());
    }

    @Test
    void setTotalPrice() throws SQLException {
        when(roomRepository.findById(1)).thenReturn(Optional.of(testRoom));
        doNothing().when(roomRepository).update(any(Room.class));

        roomService.setTotalPrice(1, BigDecimal.valueOf(6000));

        assertThat(testRoom.getPrice()).isEqualTo(BigDecimal.valueOf(6000));
        verify(roomRepository).update(testRoom);
    }

    @Test
    void setTotalPrice_ShouldThrowRoomNotFoundException() throws SQLException {
        when(roomRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.setTotalPrice(999, BigDecimal.valueOf(6000)));
        verify(roomRepository, never()).update(any());
    }

    @Test
    void setTotalPrice_WithNegativePrice_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> roomService.setTotalPrice(1, BigDecimal.valueOf(-1000)));
        verify(roomRepository, never()).findById(any());
    }

    @Test
    void setTotalPrice_WithNullPrice_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> roomService.setTotalPrice(1, null));
        verify(roomRepository, never()).findById(any());
    }

    @Test
    void changeRoomPrice() throws SQLException {
        when(roomRepository.findById(1)).thenReturn(Optional.of(testRoom));
        doNothing().when(roomRepository).update(any(Room.class));

        roomService.changeRoomPrice(1, BigDecimal.valueOf(6000));

        assertThat(testRoom.getPrice()).isEqualTo(BigDecimal.valueOf(6000));
        verify(roomRepository).update(testRoom);
    }

    @Test
    void changeRoomPrice_WhenRoomOccupied_ShouldThrowRoomNotAvailableException() throws SQLException {
        testRoom.setStatus(RoomStatus.OCCUPIED);
        when(roomRepository.findById(1)).thenReturn(Optional.of(testRoom));

        assertThrows(RoomNotAvailableException.class, () -> roomService.changeRoomPrice(1, BigDecimal.valueOf(6000)));
        verify(roomRepository, never()).update(any());
    }

    @Test
    void delete() throws SQLException {
        when(bookingsRepository.findActiveByRoomId(eq(101), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        doNothing().when(roomRepository).delete(any(Room.class));

        roomService.delete(testRoom);

        verify(bookingsRepository).findActiveByRoomId(eq(101), any(LocalDate.class));
        verify(roomRepository).delete(testRoom);
    }

    @Test
    void delete_WithNullRoom_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> roomService.delete(null));
        verify(roomRepository, never()).delete(any());
    }

    @Test
    void update() throws SQLException {
        when(roomRepository.findById(101)).thenReturn(Optional.of(testRoom));
        doNothing().when(roomRepository).update(any(Room.class));

        roomService.update(testRoom);

        verify(roomRepository).update(testRoom);
    }

    @Test
    void update_WithNullRoom_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> roomService.update(null));
        verify(roomRepository, never()).update(any());
    }

    @Test
    void update_WithNullId_ShouldThrowHotelException() throws SQLException {
        testRoom.setId(0);
        assertThrows(HotelException.class, () -> roomService.update(testRoom));
        verify(roomRepository, never()).update(any());
    }

    @Test
    void update_WithNonExistentRoom_ShouldThrowRoomNotFoundException() throws SQLException {
        when(roomRepository.findById(101)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.update(testRoom));
        verify(roomRepository, never()).update(any());
    }

    @Test
    void save() throws SQLException {
        when(roomRepository.save(any(Room.class))).thenReturn(1);

        Integer savedId = roomService.save(testRoom);

        assertThat(savedId).isEqualTo(1);
        verify(roomRepository).save(testRoom);
    }

    @Test
    void save_WithNullRoom_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> roomService.save(null));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void save_WithNullNumber_ShouldThrowHotelException() {
        testRoom.setNumber(null);
        assertThrows(HotelException.class, () -> roomService.save(testRoom));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void save_WithNullPrice_ShouldThrowHotelException() {
        testRoom.setPrice(null);
        assertThrows(HotelException.class, () -> roomService.save(testRoom));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void save_WithNegativePrice_ShouldThrowHotelException() {
        testRoom.setPrice(BigDecimal.valueOf(-1000));
        assertThrows(HotelException.class, () -> roomService.save(testRoom));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void listAvailableRooms() {
        when(roomRepository.listAvailableRooms(RoomFilter.PRICE)).thenReturn(Collections.singletonList(testRoom));

        List<Room> rooms = roomService.listAvailableRooms(RoomFilter.PRICE);

        assertThat(rooms).hasSize(1);
        verify(roomRepository).listAvailableRooms(RoomFilter.PRICE);
    }

    @Test
    void listAvailableRooms_WithNullFilter_ShouldUseDefault() {
        when(roomRepository.listAvailableRooms(null)).thenReturn(Collections.singletonList(testRoom));

        List<Room> rooms = roomService.listAvailableRooms(null);

        assertThat(rooms).hasSize(1);
        verify(roomRepository).listAvailableRooms(null);
    }

    @Test
    void listAvailableRoomsByDate() {
        LocalDate date = LocalDate.now();
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));
        when(bookingsRepository.findAll()).thenReturn(Collections.emptyList());

        List<Room> rooms = roomService.listAvailableRoomsByDate(RoomFilter.PRICE, date);

        assertThat(rooms).hasSize(1);
        verify(roomRepository).findAll();
        verify(bookingsRepository).findAll();
    }

    @Test
    void listAvailableRoomsByDate_WithNullDate_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> roomService.listAvailableRoomsByDate(RoomFilter.PRICE, null));
    }

    @Test
    void listAvailableRoomsByDate_WithFilterNull_ShouldUseDefault() {
        LocalDate date = LocalDate.now();
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));
        when(bookingsRepository.findAll()).thenReturn(Collections.emptyList());

        List<Room> rooms = roomService.listAvailableRoomsByDate(null, date);

        assertThat(rooms).hasSize(1);
    }

    @Test
    void requestListRoomAndPrice() {
        doNothing().when(roomRepository).requestListRoomAndPrice(RoomFilter.PRICE);

        roomService.requestListRoomAndPrice(RoomFilter.PRICE);

        verify(roomRepository).requestListRoomAndPrice(RoomFilter.PRICE);
    }
}