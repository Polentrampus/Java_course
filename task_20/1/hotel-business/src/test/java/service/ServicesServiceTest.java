package service;

import config.TestHibernateConfig;
import hotel.dto.CreateServiceRequest;
import hotel.exception.HotelException;
import hotel.exception.service.ServiceAlreadyExistsException;
import hotel.exception.service.ServiceNotFoundException;
import hotel.model.service.Services;
import hotel.repository.service.ServicesRepository;
import hotel.service.ServicesService;
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
public class ServicesServiceTest {

    @Mock
    private ServicesRepository servicesRepository;

    @InjectMocks
    private ServicesService servicesService;

    private Services testService;
    private CreateServiceRequest createServiceRequest;

    @BeforeEach
    public void setUp() {
        testService = new Services();
        testService.setId(1);
        testService.setName("SPA");
        testService.setDescription("Спа процедуры");
        testService.setPrice(BigDecimal.valueOf(3000));

        createServiceRequest = new CreateServiceRequest();
        createServiceRequest.setName("SPA");
        createServiceRequest.setDescription("Спа процедуры");
        createServiceRequest.setPrice(BigDecimal.valueOf(3000));
    }

    @Test
    void findById() throws SQLException {
        when(servicesRepository.findById(1)).thenReturn(Optional.of(testService));

        Optional<Services> result = servicesService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        verify(servicesRepository).findById(1);
    }

    @Test
    void findById_ShouldReturnEmpty() throws SQLException {
        when(servicesRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Services> result = servicesService.findById(999);

        assertThat(result).isEmpty();
        verify(servicesRepository).findById(999);
    }

    @Test
    void findById_WithNullId_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> servicesService.findById(null));
        verify(servicesRepository, never()).findById(any());
    }

    @Test
    void findAll() {
        when(servicesRepository.findAll()).thenReturn(Collections.singletonList(testService));

        List<Services> services = servicesService.findAll();

        assertThat(services).hasSize(1);
        assertThat(services.get(0).getId()).isEqualTo(1);
        verify(servicesRepository).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(servicesRepository.findAll()).thenReturn(Collections.emptyList());

        List<Services> services = servicesService.findAll();

        assertThat(services).isEmpty();
        verify(servicesRepository).findAll();
    }

    @Test
    void save() throws SQLException {
        when(servicesRepository.findByName("SPA")).thenReturn(Optional.empty());
        when(servicesRepository.save(any(Services.class))).thenReturn(1);
        when(servicesRepository.findById(1)).thenReturn(Optional.of(testService));

        Services result = servicesService.save(testService);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        verify(servicesRepository).save(any(Services.class));
    }

    @Test
    void save_WithExistingName_ShouldThrowServiceAlreadyExistsException() {
        when(servicesRepository.findByName("SPA")).thenReturn(Optional.of(testService));

        assertThrows(ServiceAlreadyExistsException.class, () -> servicesService.save(testService));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void save_WithNullName_ShouldThrowHotelException() {
        testService.setName(null);

        assertThrows(HotelException.class, () -> servicesService.save(testService));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void save_WithEmptyName_ShouldThrowHotelException() {
        testService.setName("");

        assertThrows(HotelException.class, () -> servicesService.save(testService));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void save_WithShortName_ShouldThrowHotelException() {
        testService.setName("A");

        assertThrows(HotelException.class, () -> servicesService.save(testService));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void save_WithLongName_ShouldThrowHotelException() {
        testService.setName("A".repeat(101));

        assertThrows(HotelException.class, () -> servicesService.save(testService));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void save_WithNegativePrice_ShouldThrowHotelException() {
        testService.setPrice(BigDecimal.valueOf(-1000));

        assertThrows(HotelException.class, () -> servicesService.save(testService));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void save_WithZeroPrice_ShouldThrowHotelException() {
        testService.setPrice(BigDecimal.ZERO);

        assertThrows(HotelException.class, () -> servicesService.save(testService));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void update() throws SQLException {
        when(servicesRepository.findById(1)).thenReturn(Optional.of(testService));
        doNothing().when(servicesRepository).update(any(Services.class));

        servicesService.update(testService);

        verify(servicesRepository).update(testService);
    }

    @Test
    void update_WithNullService_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> servicesService.update(null));
        verify(servicesRepository, never()).update(any());
    }

    @Test
    void update_WithNullId_ShouldThrowHotelException() throws SQLException {
        testService.setId(0);

        assertThrows(HotelException.class, () -> servicesService.update(testService));
        verify(servicesRepository, never()).update(any());
    }

    @Test
    void update_WithNonExistentService_ShouldThrowServiceNotFoundException() throws SQLException {
        when(servicesRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ServiceNotFoundException.class, () -> servicesService.update(testService));
        verify(servicesRepository, never()).update(any());
    }

    @Test
    void delete() throws SQLException {
        when(servicesRepository.findById(1)).thenReturn(Optional.of(testService));
        doNothing().when(servicesRepository).delete(any(Services.class));

        servicesService.delete(1);

        verify(servicesRepository).delete(testService);
    }

    @Test
    void delete_WithNullId_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> servicesService.delete(null));
        verify(servicesRepository, never()).delete(any());
    }

    @Test
    void delete_WithNonExistentId_ShouldThrowServiceNotFoundException() throws SQLException {
        when(servicesRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ServiceNotFoundException.class, () -> servicesService.delete(999));
        verify(servicesRepository, never()).delete(any());
    }

    @Test
    void addService() throws SQLException {
        when(servicesRepository.findByName("SPA")).thenReturn(Optional.empty());
        when(servicesRepository.save(any(Services.class))).thenReturn(1);
        when(servicesRepository.findById(1)).thenReturn(Optional.of(testService));

        Optional<Services> result = servicesService.addService(createServiceRequest);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        verify(servicesRepository).save(any(Services.class));
    }

    @Test
    void addService_WithExistingName_ShouldThrowServiceAlreadyExistsException() {
        when(servicesRepository.findByName("SPA")).thenReturn(Optional.of(testService));

        assertThrows(ServiceAlreadyExistsException.class, () -> servicesService.addService(createServiceRequest));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void addService_WithNullName_ShouldThrowHotelException() {
        createServiceRequest.setName(null);

        assertThrows(HotelException.class, () -> servicesService.addService(createServiceRequest));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void addService_WithEmptyName_ShouldThrowHotelException() {
        createServiceRequest.setName("");

        assertThrows(HotelException.class, () -> servicesService.addService(createServiceRequest));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void addService_WithShortName_ShouldThrowHotelException() {
        createServiceRequest.setName("A");

        assertThrows(HotelException.class, () -> servicesService.addService(createServiceRequest));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void addService_WithLongName_ShouldThrowHotelException() {
        createServiceRequest.setName("A".repeat(101));

        assertThrows(HotelException.class, () -> servicesService.addService(createServiceRequest));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void addService_WithNegativePrice_ShouldThrowHotelException() {
        createServiceRequest.setPrice(BigDecimal.valueOf(-1000));

        assertThrows(HotelException.class, () -> servicesService.addService(createServiceRequest));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void addService_WithZeroPrice_ShouldThrowHotelException() {
        createServiceRequest.setPrice(BigDecimal.ZERO);

        assertThrows(HotelException.class, () -> servicesService.addService(createServiceRequest));
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void setPrice_ById() throws SQLException {
        when(servicesRepository.findById(1)).thenReturn(Optional.of(testService));
        doNothing().when(servicesRepository).update(any(Services.class));

        servicesService.setPrice(1, BigDecimal.valueOf(5000));

        assertThat(testService.getPrice()).isEqualTo(BigDecimal.valueOf(5000));
        verify(servicesRepository).update(testService);
    }
    
    @Test
    void setPrice_ById_WithNegativePrice_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> servicesService.setPrice(1, BigDecimal.valueOf(-1000)));
        verify(servicesRepository, never()).findById(any());
    }

    @Test
    void setPrice_ById_WithZeroPrice_ShouldThrowHotelException() throws SQLException {
        assertThrows(HotelException.class, () -> servicesService.setPrice(1, BigDecimal.ZERO));
        verify(servicesRepository, never()).findById(any());
    }

    @Test
    void setPrice_ById_WithNonExistentId_ShouldThrowServiceNotFoundException() throws SQLException {
        when(servicesRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ServiceNotFoundException.class, () -> servicesService.setPrice(999, BigDecimal.valueOf(5000)));
        verify(servicesRepository, never()).update(any());
    }

    @Test
    void setPrice_ByName() throws SQLException {
        when(servicesRepository.findByName("SPA")).thenReturn(Optional.of(testService));
        doNothing().when(servicesRepository).update(any(Services.class));

        servicesService.setPrice("SPA", BigDecimal.valueOf(5000));

        assertThat(testService.getPrice()).isEqualTo(BigDecimal.valueOf(5000));
        verify(servicesRepository).update(testService);
    }

    @Test
    void setPrice_ByName_WithNullName_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> servicesService.setPrice(0, BigDecimal.valueOf(5000)));
        verify(servicesRepository, never()).findByName(any());
    }

    @Test
    void setPrice_ByName_WithEmptyName_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> servicesService.setPrice("", BigDecimal.valueOf(5000)));
        verify(servicesRepository, never()).findByName(any());
    }

    @Test
    void setPrice_ByName_WithNegativePrice_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> servicesService.setPrice("SPA", BigDecimal.valueOf(-1000)));
        verify(servicesRepository, never()).findByName(any());
    }

    @Test
    void setPrice_ByName_WithNonExistentName_ShouldThrowServiceNotFoundException() throws SQLException {
        when(servicesRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ServiceNotFoundException.class, () -> servicesService.setPrice("UNKNOWN", BigDecimal.valueOf(5000)));
        verify(servicesRepository, never()).update(any());
    }

    @Test
    void findByName() {
        when(servicesRepository.findByName("SPA")).thenReturn(Optional.of(testService));

        Optional<Services> result = servicesService.findByName("SPA");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("SPA");
        verify(servicesRepository).findByName("SPA");
    }

    @Test
    void findByName_ShouldReturnEmpty() {
        when(servicesRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        Optional<Services> result = servicesService.findByName("UNKNOWN");

        assertThat(result).isEmpty();
        verify(servicesRepository).findByName("UNKNOWN");
    }

    @Test
    void findByName_WithNullName_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> servicesService.findByName(null));
        verify(servicesRepository, never()).findByName(any());
    }

    @Test
    void findByName_WithEmptyName_ShouldThrowHotelException() {
        assertThrows(HotelException.class, () -> servicesService.findByName(""));
        verify(servicesRepository, never()).findByName(any());
    }
}