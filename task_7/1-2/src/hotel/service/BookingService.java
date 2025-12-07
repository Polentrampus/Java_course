package hotel.controller.service;

import hotel.model.Hotel;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.users.client.Client;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;


@RequiredArgsConstructor
public class BookingService {
    private Hotel hotel = Hotel.getInstance();

    public List<Bookings> getAllBookings() {
        return hotel.getBookingsMap().get().values().stream().toList();
    }

    public Bookings getBookingById(Integer id) {
        return hotel.getBookingsMap().get().get(id);
    }

    public Bookings createBooking(CreateBookingRequest request) {
        Client client = new Client();
        Room room = new Room();

        if (bookingRepository.isRoomBooked(request.getRoomId(),
                request.getCheckInDate(), request.getCheckOutDate())) {
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

        long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        double roomPrice = room.get().getPrice() * days;

        Bookings booking = new Bookings();
        booking.setClient(client.get());
        booking.setRoom(room.get());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setTotalPrice(roomPrice);

        room.get().setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room.get());

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        Bookings booking = getBookingById(id);

        // Возвращаем комнату в доступный статус, если бронирование еще активно
        if (!booking.getCheckOutDate().isBefore(LocalDate.now())) {
            Room room = booking.getRoom();
            room.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(room);
        }

        bookingRepository.deleteById(id);
    }

    public Bookings addServiceToBooking(Long bookingId, Long serviceId) {
        Bookings booking = getBookingById(bookingId);

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));

        booking.getServices().add(service);
        booking.setTotalPrice(booking.getTotalPrice() + service.getPrice());
        return bookingRepository.save(booking);
    }

    public List<Bookings> getClientBookings(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        return bookingRepository.findByClient(client);
    }

    public List<Bookings> getActiveBookings() {
        return bookingRepository.findByCheckOutDateGreaterThanEqual(LocalDate.now());
    }
}