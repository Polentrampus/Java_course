package controller;

import config.TestHibernateConfig;
import hotel.controller.BookingController;
import hotel.dto.BookingDTO;
import hotel.dto.CreateBookingRequest;
import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.room.RoomType;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.service.IBookingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Transactional
@ContextConfiguration(classes = TestHibernateConfig.class)
@ActiveProfiles("test")
public class BookingControllerTest extends BaseController {
    @Mock
    private IBookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private Bookings testBookings;
    private Client testClient;
    private Room testRoom;
    private Services testService1;
    private Services testService2;

    @BeforeEach
    public void setUp() {
        super.setUp();
        setUpMockMvc(bookingController);

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
        testService2.setId(2);
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
    void getAllBookings() throws Exception {
        when(bookingService.getAllBookings()).thenReturn(Collections.singletonList(testBookings));

        MvcResult mvcResult = mockMvc.perform(get("/bookings/findAll"))
                .andExpect(status().isOk()).andReturn();
        BookingDTO bookingDTO = BookingDTO.from(testBookings);;
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(asJsonString(List.of(bookingDTO)));
    }

    @Test
    void getActiveBookings() throws Exception {
        when(bookingService.findActiveBookings(LocalDate.now())).thenReturn(Collections.singletonList(testBookings));

        MvcResult mvcResult = mockMvc.perform(get("/bookings/active"))
                .andExpect(status().isOk()).andReturn();

        BookingDTO bookingDTO = BookingDTO.from(testBookings);;
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(asJsonString(List.of(bookingDTO)));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(1)).thenReturn(java.util.Optional.of(testBookings));

        MvcResult mvcResult = mockMvc.perform(get("/bookings/getById/{id}", 1))
                .andExpect(status().isOk()).andReturn();

        BookingDTO bookingDTO = BookingDTO.from(testBookings);;
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(asJsonString(bookingDTO));
    }

    @Test
    void getClientBookings() throws Exception {
        when(bookingService.findActiveByClientId(1)).thenReturn(Collections.singletonList(testBookings));

        MvcResult mvcResult = mockMvc.perform(get("/bookings/client/{clientId}", 1))
                .andExpect(status().isOk()).andReturn();

        BookingDTO bookingDTO = BookingDTO.from(testBookings);;
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(asJsonString(List.of(bookingDTO)));
    }

    @Test
    void createBooking() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(testBookings);
        when(bookingService.createBooking(any(CreateBookingRequest.class)))
                .thenReturn(Optional.of(testBookings));

        mockMvc.perform(post("/bookings/createBooking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.roomId").value(101));

        verify(bookingService).createBooking(any(CreateBookingRequest.class));
    }

    @Test
    void addServiceToBooking() throws Exception {
        when(bookingService.addServiceToBooking(1, List.of(1, 2))).thenReturn(Optional.of(testBookings));

        mockMvc.perform(post("/bookings/{bookingId}/services/{serviceId}", 1, "1,2"))
                .andExpect(status().isOk());

        verify(bookingService).addServiceToBooking(1, List.of(1, 2));
    }

    @Test
    void addServiceToBooking_NotFound() throws Exception {
        when(bookingService.addServiceToBooking(1, List.of(1))).thenReturn(Optional.empty());

        mockMvc.perform(post("/bookings/{bookingId}/services/{serviceId}", 1, "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBooking() throws Exception {
        when(bookingService.getBookingById(1)).thenReturn(Optional.of(testBookings));
        mockMvc.perform(delete("/bookings/delete/{id}", 1))
                .andExpect(status().isOk());

        verify(bookingService).deleteBookingById(1);
    }

    @Test
    void getBookingById_NotFound() throws Exception {
        mockMvc.perform(get("/bookings/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBooking_WithInvalidDates_ShouldReturnBadRequest() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(testBookings);
        request.setCheckInDate(LocalDate.now().plusDays(5));
        request.setCheckOutDate(LocalDate.now()); // выезд раньше заезда

        mockMvc.perform(post("/bookings/createBooking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_WithNullClientId_ShouldReturnBadRequest() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(testBookings);
        request.setClientId(null);

        mockMvc.perform(post("/bookings/createBooking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_WithNullRoomId_ShouldReturnBadRequest() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(testBookings);
        request.setRoomId(null);

        mockMvc.perform(post("/bookings/createBooking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_WithNullCheckInDate_ShouldReturnBadRequest() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(testBookings);
        request.setCheckInDate(null);

        mockMvc.perform(post("/bookings/createBooking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_WithNullCheckOutDate_ShouldReturnBadRequest() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(testBookings);
        request.setCheckOutDate(null);

        mockMvc.perform(post("/bookings/createBooking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addServiceToBooking_WithEmptyServiceList_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings/{bookingId}/services/{serviceId}", 1, ""))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBooking_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/bookings/delete/{id}", 999))
                .andExpect(status().isNotFound());
    }
}
