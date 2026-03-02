package hotel.controller;

import hotel.dto.ClientDto;
import hotel.dto.CreateClientRequest;
import hotel.model.users.client.Client;
import hotel.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor  // Автоматически создает конструктор с final полями
public class ClientController {
    private final ClientService clientService;

    @GetMapping
    @Operation(
            summary = "Получить всех клиентов",
            description = "Возвращает список всех клиентов отеля. "
    )

    public ResponseEntity<List<ClientDto>> getAllClients() {
        log.info("Получение списка всех клиентов");
        List<Client> clients = clientService.findAll();
        List<ClientDto> responses = clients.stream().
                map(ClientDto::from).toList();
        log.debug("Найдено клиентов: {}", clients.size());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить клиента по ID",
            description = "Возвращает информацию о конкретном клиенте по его идентификатору."
    )

    public ResponseEntity<ClientDto> getClientById(@PathVariable(name = "id") Integer id) throws SQLException {
        log.info("Получение клиента по ID: {}", id);
        Optional<Client> client = clientService.findById(id);
        if (client.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(client.map(ClientDto::from).get());
    }

    @PostMapping("/createClient")
    @Operation(
            summary = "Создать нового клиента",
            description = """
            Создание нового клиента отеля.
            Требуются уникальные номер телефона и паспорта.
            """
    )

    public ResponseEntity<ClientDto> addClient(@Valid @RequestBody CreateClientRequest request) throws SQLException {
        log.info("Создание нового клиента: Имя={}",
                request.getName());
        log.debug("Данные клиента: {}", request);

        Optional<Client> client = clientService.save(request);
        if (client.isEmpty()) return ResponseEntity.notFound().build();
        log.info("Клиент успешно создан: ID={}, Имя={}", client.get().getId(), client.get().getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/clients/" + client.get().getId())
                .body(client.map(ClientDto::from).get());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить клиента",
            description = "Удаление клиента по ID."
    )

    public ResponseEntity<Void> deleteClient(@PathVariable(name = "id") Integer id) throws SQLException {
        log.info("Удаление клиента ID: {}", id);
        clientService.delete(id);
        log.info("Клиент ID: {} успешно удален", id);
        return ResponseEntity.noContent().build();
    }
}
