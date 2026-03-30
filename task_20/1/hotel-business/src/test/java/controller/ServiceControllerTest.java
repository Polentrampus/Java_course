package controller;

import config.TestHibernateConfig;
import hotel.controller.ServiceController;
import hotel.dto.CreateServiceRequest;
import hotel.dto.ServiceDto;
import hotel.model.filter.ServicesFilter;
import hotel.model.service.Services;
import hotel.service.ServicesService;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
public class ServiceControllerTest extends BaseController {

    @Mock
    private ServicesService servicesService;

    @InjectMocks
    private ServiceController serviceController;

    private Services testService;
    private CreateServiceRequest createServiceRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();
        setUpMockMvc(serviceController);

        testService = new Services();
        testService.setId(1);
        testService.setName("SPA");
        testService.setDescription("Spa procedures");
        testService.setPrice(BigDecimal.valueOf(3000));

        createServiceRequest = new CreateServiceRequest();
        createServiceRequest.setName("SPA");
        createServiceRequest.setDescription("Spa procedures");
        createServiceRequest.setPrice(BigDecimal.valueOf(3000));
    }

    @Test
    void getAllServices() throws Exception {
        when(servicesService.findAll()).thenReturn(Collections.singletonList(testService));

        MvcResult mvcResult = mockMvc.perform(get("/services/findAll"))
                .andExpect(status().isOk())
                .andReturn();

        ServiceDto serviceDto = ServiceDto.from(testService);
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(asJsonString(List.of(serviceDto)));
    }

    @Test
    void searchServices() throws Exception {
        when(servicesService.findByName("SPA")).thenReturn(Optional.of(testService));

        MvcResult mvcResult = mockMvc.perform(get("/services/search")
                        .param("name", "SPA"))
                .andExpect(status().isOk())
                .andReturn();

        ServiceDto serviceDto = ServiceDto.from(testService);
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(asJsonString(serviceDto));
    }

    @Test
    void searchServices_NotFound() throws Exception {
        when(servicesService.findByName("UNKNOWN")).thenReturn(Optional.empty());

        mockMvc.perform(get("/services/search")
                        .param("name", "UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getServiceById() throws Exception {
        when(servicesService.findById(1)).thenReturn(Optional.of(testService));

        MvcResult mvcResult = mockMvc.perform(get("/services/getById/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        ServiceDto serviceDto = ServiceDto.from(testService);
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(asJsonString(serviceDto));
    }

    @Test
    void getServiceById_NotFound() throws Exception {
        when(servicesService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/services/getById/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void createService() throws Exception {
        when(servicesService.addService(any(CreateServiceRequest.class)))
                .thenReturn(Optional.of(testService));

        mockMvc.perform(post("/services/createService")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createServiceRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("SPA"))
                .andExpect(jsonPath("$.price").value(3000));

        verify(servicesService).addService(any(CreateServiceRequest.class));
    }

    @Test
    void deleteService() throws Exception {
        when(servicesService.findById(1)).thenReturn(Optional.of(testService));

        mockMvc.perform(delete("/services/delete/{id}", 1))
                .andExpect(status().isNoContent());

        verify(servicesService).delete(1);
    }

    @Test
    void deleteService_NotFound() throws Exception {
        when(servicesService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/services/delete/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void createService_WithNullName_ShouldReturnBadRequest() throws Exception {
        CreateServiceRequest invalidRequest = new CreateServiceRequest();
        invalidRequest.setName(null);
        invalidRequest.setDescription("Spa procedures");
        invalidRequest.setPrice(BigDecimal.valueOf(3000));

        mockMvc.perform(post("/services/createService")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createService_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        CreateServiceRequest invalidRequest = new CreateServiceRequest();
        invalidRequest.setName("");
        invalidRequest.setDescription("Spa procedures");
        invalidRequest.setPrice(BigDecimal.valueOf(3000));

        mockMvc.perform(post("/services/createService")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createService_WithNullPrice_ShouldReturnBadRequest() throws Exception {
        CreateServiceRequest invalidRequest = new CreateServiceRequest();
        invalidRequest.setName("SPA");
        invalidRequest.setDescription("Spa procedures");
        invalidRequest.setPrice(null);

        mockMvc.perform(post("/services/createService")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createService_WithNegativePrice_ShouldReturnBadRequest() throws Exception {
        CreateServiceRequest invalidRequest = new CreateServiceRequest();
        invalidRequest.setName("SPA");
        invalidRequest.setDescription("Spa procedures");
        invalidRequest.setPrice(BigDecimal.valueOf(-1000));

        mockMvc.perform(post("/services/createService")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createService_WithZeroPrice_ShouldReturnBadRequest() throws Exception {
        CreateServiceRequest invalidRequest = new CreateServiceRequest();
        invalidRequest.setName("SPA");
        invalidRequest.setDescription("Spa procedures");
        invalidRequest.setPrice(BigDecimal.ZERO);

        mockMvc.perform(post("/services/createService")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}