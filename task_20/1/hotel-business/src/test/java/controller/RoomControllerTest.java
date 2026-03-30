package controller;

import config.TestHibernateConfig;
import hotel.controller.RoomController;
import hotel.dto.CreateRoomRequest;
import hotel.dto.RoomDto;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.service.IRoomService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ContextConfiguration(classes = TestHibernateConfig.class)
@ActiveProfiles("test")
public class RoomControllerTest extends BaseController {

    @Mock
    private IRoomService roomService;

    @InjectMocks
    private RoomController roomController;

    private Room testRoom;
    private CreateRoomRequest createRoomRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();
        setUpMockMvc(roomController);

        testRoom = new Room();
        testRoom.setId(1);
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
    void getAllRooms() throws Exception {
        when(roomService.sortRooms(RoomFilter.ID)).thenReturn(Collections.singletonList(testRoom));

        MvcResult mvcResult = mockMvc.perform(get("/rooms/public/findAll"))
                .andExpect(status().isOk())
                .andReturn();

        RoomDto roomDto = RoomDto.from(testRoom);
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(asJsonString(List.of(roomDto)));
    }

    @Test
    void getAllRoomsWithSorting() throws Exception {
        when(roomService.sortRooms(RoomFilter.PRICE)).thenReturn(Collections.singletonList(testRoom));

        MvcResult mvcResult = mockMvc.perform(get("/rooms/public/findAll")
                        .param("sortBy", "PRICE"))
                .andExpect(status().isOk())
                .andReturn();

        RoomDto roomDto = RoomDto.from(testRoom);
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(asJsonString(List.of(roomDto)));
    }

    @Test
    void getAvailableRooms() throws Exception {
        LocalDate date = LocalDate.now();
        when(roomService.listAvailableRoomsByDate(RoomFilter.ID, date))
                .thenReturn(Collections.singletonList(testRoom));

        MvcResult mvcResult = mockMvc.perform(get("/rooms/public/available")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andReturn();

        RoomDto roomDto = RoomDto.from(testRoom);
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(asJsonString(List.of(roomDto)));
    }

    @Test
    void getAvailableRoomsWithSorting() throws Exception {
        LocalDate date = LocalDate.now();
        when(roomService.listAvailableRoomsByDate(RoomFilter.CAPACITY, date))
                .thenReturn(Collections.singletonList(testRoom));

        MvcResult mvcResult = mockMvc.perform(get("/rooms/public/available")
                        .param("sortBy", "CAPACITY")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andReturn();

        RoomDto roomDto = RoomDto.from(testRoom);
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(asJsonString(List.of(roomDto)));
    }

    @Test
    void createRoom() throws Exception {
        when(roomService.addRoom(any(CreateRoomRequest.class)))
                .thenReturn(Optional.of(testRoom));

        mockMvc.perform(post("/rooms/createRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createRoomRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value(101))
                .andExpect(jsonPath("$.price").value(5000));

        verify(roomService).addRoom(any(CreateRoomRequest.class));
    }

    @Test
    void createRoom_BadRequest() throws Exception {
        when(roomService.addRoom(any(CreateRoomRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/rooms/createRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createRoomRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoomStatus() throws Exception {
        mockMvc.perform(patch("/rooms/{id}/status", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(RoomStatus.OCCUPIED)))
                .andExpect(status().isNoContent());

        verify(roomService).setStatusRoom(eq(1), eq(RoomStatus.OCCUPIED));
    }

    @Test
    void updateRoomPrice() throws Exception {
        when(roomService.findById(1)).thenReturn(Optional.of(testRoom));
        BigDecimal newPrice = BigDecimal.valueOf(6000);
        mockMvc.perform(patch("/rooms/{id}/price", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newPrice)))
                .andExpect(status().isNoContent());

        verify(roomService).setTotalPrice(eq(1), eq(newPrice));
    }

    @Test
    void createRoom_WithNullPrice_ShouldReturnBadRequest() throws Exception {
        CreateRoomRequest invalidRequest = new CreateRoomRequest();
        invalidRequest.setIdRoom(101);
        invalidRequest.setRoomCategory(RoomCategory.ECONOMY);
        invalidRequest.setRoomType(RoomType.STANDARD);
        invalidRequest.setRoomStatus(RoomStatus.AVAILABLE);
        invalidRequest.setPrice(null);
        invalidRequest.setCapacity(2);

        mockMvc.perform(post("/rooms/createRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoom_WithNegativePrice_ShouldReturnBadRequest() throws Exception {
        CreateRoomRequest invalidRequest = new CreateRoomRequest();
        invalidRequest.setIdRoom(101);
        invalidRequest.setRoomCategory(RoomCategory.ECONOMY);
        invalidRequest.setRoomType(RoomType.STANDARD);
        invalidRequest.setRoomStatus(RoomStatus.AVAILABLE);
        invalidRequest.setPrice(BigDecimal.valueOf(-1000));
        invalidRequest.setCapacity(2);

        mockMvc.perform(post("/rooms/createRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoom_WithNullCapacity_ShouldReturnBadRequest() throws Exception {
        CreateRoomRequest invalidRequest = new CreateRoomRequest();
        invalidRequest.setIdRoom(101);
        invalidRequest.setRoomCategory(RoomCategory.ECONOMY);
        invalidRequest.setRoomType(RoomType.STANDARD);
        invalidRequest.setRoomStatus(RoomStatus.AVAILABLE);
        invalidRequest.setPrice(BigDecimal.valueOf(5000));
        invalidRequest.setCapacity(0);

        mockMvc.perform(post("/rooms/createRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoom_WithNegativeCapacity_ShouldReturnBadRequest() throws Exception {
        CreateRoomRequest invalidRequest = new CreateRoomRequest();
        invalidRequest.setIdRoom(101);
        invalidRequest.setRoomCategory(RoomCategory.ECONOMY);
        invalidRequest.setRoomType(RoomType.STANDARD);
        invalidRequest.setRoomStatus(RoomStatus.AVAILABLE);
        invalidRequest.setPrice(BigDecimal.valueOf(5000));
        invalidRequest.setCapacity(-1);

        mockMvc.perform(post("/rooms/createRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoom_WithNullRoomCategory_ShouldReturnBadRequest() throws Exception {
        CreateRoomRequest invalidRequest = new CreateRoomRequest();
        invalidRequest.setIdRoom(101);
        invalidRequest.setRoomCategory(null);
        invalidRequest.setRoomType(RoomType.STANDARD);
        invalidRequest.setRoomStatus(RoomStatus.AVAILABLE);
        invalidRequest.setPrice(BigDecimal.valueOf(5000));
        invalidRequest.setCapacity(2);

        mockMvc.perform(post("/rooms/createRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoom_WithNullRoomType_ShouldReturnBadRequest() throws Exception {
        CreateRoomRequest invalidRequest = new CreateRoomRequest();
        invalidRequest.setIdRoom(101);
        invalidRequest.setRoomCategory(RoomCategory.ECONOMY);
        invalidRequest.setRoomType(null);
        invalidRequest.setRoomStatus(RoomStatus.AVAILABLE);
        invalidRequest.setPrice(BigDecimal.valueOf(5000));
        invalidRequest.setCapacity(2);

        mockMvc.perform(post("/rooms/createRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoomPrice_WithNegativePrice_ShouldReturnBadRequest() throws Exception {
        BigDecimal negativePrice = BigDecimal.valueOf(-1000);

        mockMvc.perform(patch("/rooms/{id}/price", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(negativePrice)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoomPrice_WithNullPrice_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/rooms/{id}/price", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoomStatus_WithNullStatus_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/rooms/{id}/status", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(null)))
                .andExpect(status().isBadRequest());
    }
}