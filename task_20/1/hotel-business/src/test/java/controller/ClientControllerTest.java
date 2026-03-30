package controller;

import config.TestHibernateConfig;
import hotel.controller.ClientController;
import hotel.dto.ClientDto;
import hotel.dto.CreateClientRequest;
import hotel.model.users.client.Client;
import hotel.service.ClientService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ContextConfiguration(classes = TestHibernateConfig.class)
@ActiveProfiles("test")
public class ClientControllerTest extends BaseController {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private Client testClient;
    private CreateClientRequest createClientRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();
        setUpMockMvc(clientController);

        testClient = new Client();
        testClient.setId(1);
        testClient.setName("Ivan");
        testClient.setSurname("Ivanov");
        testClient.setPatronymic("Ivanovich");
        testClient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testClient.setNotes("test client");

        createClientRequest = new CreateClientRequest();
        createClientRequest.setName("Ivan");
        createClientRequest.setSurname("Ivanov");
        createClientRequest.setPatronymic("Ivanovich");
        createClientRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    @Test
    void getAllClients() throws Exception {
        when(clientService.findAll()).thenReturn(Collections.singletonList(testClient));

        MvcResult mvcResult = mockMvc.perform(get("/clients/findAll"))
                .andExpect(status().isOk())
                .andReturn();

        ClientDto clientDto = ClientDto.from(testClient);
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(asJsonString(List.of(clientDto)));
    }

    @Test
    void getClientById() throws Exception {
        when(clientService.findById(1)).thenReturn(Optional.of(testClient));

        MvcResult mvcResult = mockMvc.perform(get("/clients/getById/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        ClientDto clientDto = ClientDto.from(testClient);
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(asJsonString(clientDto));
    }

    @Test
    void getClientById_NotFound() throws Exception {
        when(clientService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/clients/getById/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void createClient() throws Exception {
        when(clientService.save(any(CreateClientRequest.class)))
                .thenReturn(Optional.of(testClient));

        mockMvc.perform(post("/clients/createClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createClientRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.surname").value("Ivanov"));

        verify(clientService).save(any(CreateClientRequest.class));
    }

    @Test
    void createClient_BadRequest() throws Exception {
        when(clientService.save(any(CreateClientRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/clients/createClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createClientRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteClient() throws Exception {
        mockMvc.perform(delete("/clients/delete/{id}", 1))
                .andExpect(status().isNoContent());

        verify(clientService).delete(1);
    }

    @Test
    void createClient_WithInvalidDateOfBirth_ShouldReturnBadRequest() throws Exception {
        CreateClientRequest invalidRequest = new CreateClientRequest();
        invalidRequest.setName("Ivan");
        invalidRequest.setSurname("Ivanov");
        invalidRequest.setPatronymic("Ivanovich");
        invalidRequest.setDateOfBirth(LocalDate.now().plusDays(1)); // дата в будущем

        mockMvc.perform(post("/clients/createClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClient_WithNullName_ShouldReturnBadRequest() throws Exception {
        CreateClientRequest invalidRequest = new CreateClientRequest();
        invalidRequest.setName(null);
        invalidRequest.setSurname("Ivanov");
        invalidRequest.setPatronymic("Ivanovich");
        invalidRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/clients/createClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClient_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        CreateClientRequest invalidRequest = new CreateClientRequest();
        invalidRequest.setName("");
        invalidRequest.setSurname("Ivanov");
        invalidRequest.setPatronymic("Ivanovich");
        invalidRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/clients/createClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClient_WithNullSurname_ShouldReturnBadRequest() throws Exception {
        CreateClientRequest invalidRequest = new CreateClientRequest();
        invalidRequest.setName("Ivan");
        invalidRequest.setSurname(null);
        invalidRequest.setPatronymic("Ivanovich");
        invalidRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/clients/createClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClient_WithEmptySurname_ShouldReturnBadRequest() throws Exception {
        CreateClientRequest invalidRequest = new CreateClientRequest();
        invalidRequest.setName("Ivan");
        invalidRequest.setSurname("");
        invalidRequest.setPatronymic("Ivanovich");
        invalidRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/clients/createClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClient_WithNullDateOfBirth_ShouldReturnBadRequest() throws Exception {
        CreateClientRequest invalidRequest = new CreateClientRequest();
        invalidRequest.setName("Ivan");
        invalidRequest.setSurname("Ivanov");
        invalidRequest.setPatronymic("Ivanovich");
        invalidRequest.setDateOfBirth(null);

        mockMvc.perform(post("/clients/createClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

}