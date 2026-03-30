package hotel.controller;

import hotel.dto.CreateServiceRequest;
import hotel.dto.ServiceDto;
import hotel.model.service.Services;
import hotel.service.ServicesService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServicesService servicesService;

    @GetMapping("/findAll")
    @Operation(
            summary = "Получить все услуги",
            description = """
            Возвращает полный список всех доступных услуг отеля.
            """
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'EMPLOYEE')")
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        log.info("Получение списка всех услуг");
        List<Services> services = servicesService.findAll();
        List<ServiceDto> response = services.stream().map(ServiceDto::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Поиск услуг",
            description = """
            Поиск услуг по названию и/или описанию.
            """
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'EMPLOYEE')")
    public ResponseEntity<ServiceDto> searchServices(
            @RequestParam(name = "name", required = false) String name) {
        log.info("Поиск услуг: имя={}", name);
        Optional<Services> service = servicesService.findByName(name);
        if(service.isEmpty()) return ResponseEntity.notFound().build();
        log.debug("Найдена услуга по запросу: {}", service.toString());
        return ResponseEntity.ok(service.map(ServiceDto::from).get());
    }

    @GetMapping("/getById/{id}")
    @Operation(
            summary = "Получить услугу по ID",
            description = """
            Получение детальной информации о конкретной услуге по её идентификатору.
            """
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'EMPLOYEE')")
    public ResponseEntity<ServiceDto> getServiceById(@PathVariable(name = "id") Integer id) throws SQLException {
        log.info("Получение услуги по ID: {}", id);
        Optional<Services> service = servicesService.findById(id);
        if (service.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(service.map(ServiceDto::from).get());
    }

    @PostMapping("/createService")
    @Operation(
            summary = "Создать новую услугу",
            description = """
            Создание новой дополнительной услуги в отеле.
            
            ### Пример JSON запроса:
            ```json
            {
              "name": "SPA",
              "description": "Возможность посещать спа 2 раза в неделю",
              "price": 750.0
            }
            ```

            """
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ServiceDto> createService(@Valid @RequestBody CreateServiceRequest request) throws SQLException {
        log.info("Создание новой услуги: Название={}, Цена={}",
                request.getName(), request.getPrice());
        log.debug("Данные услуги: {}", request);

        Optional<Services> service = servicesService.addService(request);
        log.info("Услуга успешно создана: ID={}, Название={}",
                service.get().getId(), service.get().getName());
        return ResponseEntity.status(201)
                .header("Location", "/api/service/" + service.get().getId())
                .body(service.map(ServiceDto::from).get());
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Удалить услугу",
            description = """
            Удаление услуги из системы.
            """
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable(name = "id") Integer id) throws SQLException {
        log.info("Удаление услуги ID: {}", id);
        if (servicesService.findById(id).isEmpty()) {
            log.warn("Услуга с ID {} не найдена для удаления", id);
            return ResponseEntity.notFound().build();
        }
        servicesService.delete(id);
        log.info("Услуга ID: {} успешно удалена", id);
        return ResponseEntity.noContent().build();
    }
}