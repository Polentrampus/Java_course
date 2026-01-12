package hotel.service;

import hotel.annotation.Component;
import hotel.dto.CreateBookingRequest;
import hotel.model.Hotel;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.model.users.client.Client;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class BookingService implements IBookingService {
    private final Hotel hotel = Hotel.getInstance();

    public BookingService() {
    }

    @Override
    public List<Bookings> getAllBookings() {
        if (hotel.getBookingsMap().isEmpty()) {
            return Collections.emptyList();
        }
        return hotel.getBookingsMap().get().values().stream().toList();
    }

    @Override
    public Optional<Bookings> getBookingById(Integer id) {
        if (hotel.getBookingsMap().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(hotel.getBookingsMap().get().get(id));
    }

    @Override
    public Optional<Bookings> createBooking(CreateBookingRequest request) {
        Optional<Client> client = Optional.of(new Client());
        Optional<Room> room = Optional.of(new Room());
        room = hotel.getRoom(request.getRoomId());
        client = hotel.getClient(request.getClientId());

        if (room.isEmpty() || client.isEmpty()) {
            System.out.println("Room or Client is empty");
            return Optional.empty();
        }


        long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        double roomPrice = room.get().getPrice() * days;

        Bookings booking = new Bookings();
        booking.setId(client.get().getId());
        booking.setClient(client.get());
        booking.setRoom(room.get());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setTotalPrice(roomPrice);

        room.get().setStatus(RoomStatus.OCCUPIED);

        if (hotel.getBookingsMap().isEmpty()) {
            System.out.println("Нет записей!");
            return Optional.empty();
        }

        hotel.getBookingsMap().get().put(request.getClientId(), booking);
        return Optional.of(booking);
    }

    @Override
    public void deleteBookingById(Integer id) {
        if (hotel.getBookingsMap().isEmpty()) {
            return;
        }
        hotel.getBookingsMap().get().remove(id);
        System.out.println("Вы успешно удалили запись!");
    }

    @Override
    public Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) {
        Optional<Client> client = Optional.of(new Client());
        Optional<Room> room = Optional.of(new Room());
        room = hotel.getRoom(request.getRoomId());
        client = hotel.getClient(request.getClientId());
        if (room.isEmpty() || client.isEmpty()) {
            System.out.println("Room or Client is empty");
            return Optional.empty();
        }
        if (hotel.getBookingsActive(room.get().getNumber(), request.getCheckInDate()).isPresent()) {
            throw new RuntimeException("Комната уже забронирована на эти даты");
        }

        if (room.get().getStatus() != RoomStatus.AVAILABLE) {
            throw new RuntimeException("Комната недоступна для бронирования");
        }

        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Дата заезда не может быть в прошлом");
        }

        if (request.getCheckOutDate().isBefore(request.getCheckInDate())) {
            throw new RuntimeException("Дата выезда должна быть после даты заезда");
        }
        if (hotel.getBookingsMap().isEmpty()) {
            System.out.println("Нет записей!");
            return Optional.empty();
        }
        if (hotel.getBookingsMap().get().get(idBooking) == null) {
            System.out.println("Такой записи не существует!");
            return Optional.empty();
        }
        long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        double roomPrice = room.get().getPrice() * days;

        Bookings booking = hotel.getBookingsMap().get().get(idBooking);
        booking.setClient(client.get());
        booking.setRoom(room.get());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setTotalPrice(roomPrice);
        return Optional.of(booking);
    }
}