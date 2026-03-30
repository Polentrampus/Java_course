package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
public abstract class BaseController {
    protected ObjectMapper objectMapper; //конвертирует Java-объеты
    protected MockMvc mockMvc;  //эмулирует http запросы к контроллеру

    @BeforeEach
    public void setUp() {
        objectMapper = JsonMapper.builder()
                .build();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    /// Настраиваем mockMvc для конкретного контроллера, не загружая весь спринг контекст
    protected void setUpMockMvc(Object controller){
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /// Конвертирует Java-объект в JSON строку
    protected String asJsonString(Object object){
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
