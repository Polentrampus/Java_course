package service;

import config.TestHibernateConfig;
import hotel.dto.CreateClientRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.client.ClientException;
import hotel.exception.client.ClientNotFoundException;
import hotel.exception.dao.DAOException;
import hotel.model.filter.ClientFilter;
import hotel.model.users.client.Client;
import hotel.repository.client.ClientRepository;
import hotel.service.ClientService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestHibernateConfig.class)
@Transactional
@ActiveProfiles("test")
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client testClient;
    private CreateClientRequest createClientRequest;

    @BeforeEach
    public void setUp() {
        testClient = new Client();
        testClient.setId(1);
        testClient.setName("Иван");
        testClient.setSurname("Иванов");
        testClient.setPatronymic("Иванович");
        testClient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testClient.setNotes("Тестовый клиент");

        createClientRequest = new CreateClientRequest();
        createClientRequest.setName("Иван");
        createClientRequest.setSurname("Иванов");
        createClientRequest.setPatronymic("Иванович");
        createClientRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    @Test
    void getInfoAboutClient() throws SQLException {
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));

        String result = clientService.getInfoAboutClient(1);

        assertThat(result).isNotNull();
        assertThat(result).contains("Иван");
        verify(clientRepository).findById(1);
    }

    @Test
    void getInfoAboutClient_ShouldThrowClientNotFoundException() throws SQLException {
        when(clientRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.getInfoAboutClient(999));
        verify(clientRepository).findById(999);
    }

    @Test
    void getInfoAboutClient_WithNullId_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> clientService.getInfoAboutClient(null));
        verify(clientRepository, never()).findById(any());
    }

    @Test
    void getInfoAboutClientDatabase() {
        when(clientRepository.findAll()).thenReturn(Collections.singletonList(testClient));

        List<Client> clients = clientService.getInfoAboutClientDatabase(ClientFilter.ID);

        assertThat(clients).hasSize(1);
        assertThat(clients.get(0).getId()).isEqualTo(1);
        verify(clientRepository).findAll();
    }

    @Test
    void getInfoAboutClientDatabase_ShouldReturnEmptyList() {
        when(clientRepository.findAll()).thenReturn(Collections.emptyList());

        List<Client> clients = clientService.getInfoAboutClientDatabase(ClientFilter.ID);

        assertThat(clients).isEmpty();
        verify(clientRepository).findAll();
    }

    @Test
    void sortClient() {
        when(clientRepository.findAll()).thenReturn(Collections.singletonList(testClient));

        List<Client> clients = clientService.sortClient(ClientFilter.ID);

        assertThat(clients).hasSize(1);
        verify(clientRepository).findAll();
    }

    @Test
    void sortClient_WithNullFilter_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> clientService.sortClient(null));
        verify(clientRepository, never()).findAll();
    }

    @Test
    void requestLastThreeClient_WithEmptyList_ShouldNotThrowException() {
        when(clientRepository.findAll()).thenReturn(Collections.emptyList());

        clientService.requestLastThreeClient();

        verify(clientRepository).findAll();
    }

    @Test
    void findById() throws SQLException {
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));

        Optional<Client> result = clientService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        verify(clientRepository).findById(1);
    }

    @Test
    void findById_ShouldReturnEmpty() throws SQLException {
        when(clientRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Client> result = clientService.findById(999);

        assertThat(result).isEmpty();
        verify(clientRepository).findById(999);
    }

    @Test
    void findById_WithNullId_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> clientService.findById(null));
        verify(clientRepository, never()).findById(any());
    }

    @Test
    void findAll() {
        when(clientRepository.findAll()).thenReturn(Collections.singletonList(testClient));

        List<Client> clients = clientService.findAll();

        assertThat(clients).hasSize(1);
        assertThat(clients.get(0).getId()).isEqualTo(1);
        verify(clientRepository).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(clientRepository.findAll()).thenReturn(Collections.emptyList());

        List<Client> clients = clientService.findAll();

        assertThat(clients).isEmpty();
        verify(clientRepository).findAll();
    }

    @Test
    void save() throws SQLException {
        when(clientRepository.save(any(Client.class))).thenReturn(1);
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));

        Optional<Client> result = clientService.save(createClientRequest);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getName()).isEqualTo("Иван");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void save_WithNullName_ShouldThrowClientException() {
        createClientRequest.setName(null);

        assertThrows(ClientException.class, () -> clientService.save(createClientRequest));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void save_WithEmptyName_ShouldThrowClientException() {
        createClientRequest.setName("");

        assertThrows(ClientException.class, () -> clientService.save(createClientRequest));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void save_WithNullSurname_ShouldThrowClientException() {
        createClientRequest.setSurname(null);

        assertThrows(ClientException.class, () -> clientService.save(createClientRequest));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void save_WithEmptySurname_ShouldThrowClientException() {
        createClientRequest.setSurname("");

        assertThrows(ClientException.class, () -> clientService.save(createClientRequest));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void save_WithNullDateOfBirth_ShouldThrowClientException() {
        createClientRequest.setDateOfBirth(null);

        assertThrows(ClientException.class, () -> clientService.save(createClientRequest));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void save_WithFutureDateOfBirth_ShouldThrowClientException() {
        createClientRequest.setDateOfBirth(LocalDate.now().plusDays(1));

        assertThrows(ClientException.class, () -> clientService.save(createClientRequest));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void update() throws SQLException {
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        doNothing().when(clientRepository).update(any(Client.class));

        clientService.update(testClient);

        verify(clientRepository).update(testClient);
    }

    @Test
    void update_WithNullClient_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> clientService.update(null));
        verify(clientRepository, never()).update(any());
    }

    @Test
    void update_WithNullId_ShouldThrowHotelException() throws SQLException {
        testClient.setId(0);

        assertThrows(HotelException.class, () -> clientService.update(testClient));
        verify(clientRepository, never()).update(any());
    }

    @Test
    void update_WithNonExistentClient_ShouldThrowClientNotFoundException() throws SQLException {
        when(clientRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.update(testClient));
        verify(clientRepository, never()).update(any());
    }

    @Test
    void delete() throws SQLException {
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        doNothing().when(clientRepository).delete(any(Client.class));

        clientService.delete(1);

        verify(clientRepository).delete(testClient);
    }

    @Test
    void delete_WithNullId_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> clientService.delete(null));
        verify(clientRepository, never()).delete(any());
    }

    @Test
    void delete_WithNonExistentId_ShouldThrowClientNotFoundException() throws SQLException {
        when(clientRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.delete(999));
        verify(clientRepository, never()).delete(any());
    }
}