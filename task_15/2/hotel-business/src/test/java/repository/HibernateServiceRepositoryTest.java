package repository;
import hotel.database.TransactionManager;
import hotel.model.service.Services;
import hotel.repository.service.HibernateServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestHibernateConfig.class)
@ActiveProfiles("test")
class HibernateServiceRepositoryTest {

    @Autowired
    private HibernateServiceRepository serviceRepository;

    @Autowired
    private TransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        transactionManager.executeInTransaction(() -> {
            serviceRepository.findAll().forEach(service ->
                    serviceRepository.delete(service)
            );
            return null;
        });
    }

    @Test
    void save_ShouldPersistService() {
        Services service = new Services();
        service.setName("Wi-Fi");
        service.setPrice(BigDecimal.valueOf(10.0));
        service.setDescription("вай-фай в номере");

        Integer id = transactionManager.executeInTransaction(() ->
                serviceRepository.save(service)
        );

        assertThat(id).isNotNull();

        Optional<Services> found = transactionManager.executeInTransaction(() ->
                serviceRepository.findById(id)
        );

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Wi-Fi");
        assertThat(found.get().getPrice().compareTo(BigDecimal.valueOf(10.0))).isEqualTo(0);
    }

    @Test
    void findByName_ShouldReturnService_WhenExists() {
        Services service = new Services();
        service.setName("Breakfast");
        service.setPrice(BigDecimal.valueOf(15.0));
        service.setDescription("Еда на завтрак включена");

        transactionManager.executeInTransaction(() -> {
            serviceRepository.save(service);
            return null;
        });

        Optional<Services> found = transactionManager.executeInTransaction(() ->
                serviceRepository.findByName("Breakfast")
        );

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Breakfast");
        assertThat(found.get().getPrice().compareTo(BigDecimal.valueOf(15.0))).isEqualTo(0);
    }

    @Test
    void findByName_ShouldReturnEmpty_WhenNotExists() {
        Optional<Services> found = transactionManager.executeInTransaction(() ->
                serviceRepository.findByName("Какой-то")
        );

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllServices() {
        Services service1 = new Services();
        service1.setName("Wi-Fi");
        service1.setPrice(BigDecimal.valueOf(10.0));

        Services service2 = new Services();
        service2.setName("Breakfast");
        service2.setPrice(BigDecimal.valueOf(15.0));

        transactionManager.executeInTransaction(() -> {
            serviceRepository.save(service1);
            serviceRepository.save(service2);
            return null;
        });

        var allServices = transactionManager.executeInTransaction(() ->
                serviceRepository.findAll()
        );

        assertThat(allServices).hasSize(2);
        assertThat(allServices).extracting(Services::getName)
                .containsExactlyInAnyOrder("Wi-Fi", "Breakfast");
    }

    @Test
    void delete_ShouldRemoveService() {
        Services service = new Services();
        service.setName("ToBeDeleted");
        service.setPrice(BigDecimal.valueOf(100.0));

        Integer id = transactionManager.executeInTransaction(() ->
                serviceRepository.save(service)
        );

        transactionManager.executeInTransaction(() -> {
            Services toDelete = serviceRepository.findById(id).orElseThrow();
            serviceRepository.delete(toDelete);
            return null;
        });

        Optional<Services> found = transactionManager.executeInTransaction(() ->
                serviceRepository.findById(id)
        );
        assertThat(found).isEmpty();
    }

    @Test
    void update_ShouldModifyService() {
        Services service = new Services();
        service.setName("Original");
        service.setPrice(BigDecimal.valueOf(50.0));

        Integer id = transactionManager.executeInTransaction(() ->
                serviceRepository.save(service)
        );

        transactionManager.executeInTransaction(() -> {
            Services toUpdate = serviceRepository.findById(id).orElseThrow();
            toUpdate.setName("Updated");
            toUpdate.setPrice(BigDecimal.valueOf(75.0));
            serviceRepository.update(toUpdate);
            return null;
        });

        Optional<Services> found = transactionManager.executeInTransaction(() ->
                serviceRepository.findById(id)
        );
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Updated");
        assertThat(found.get().getPrice().compareTo(BigDecimal.valueOf(75.0))).isEqualTo(0);
    }
}