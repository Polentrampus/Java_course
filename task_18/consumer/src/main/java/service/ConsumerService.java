package service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    private final Logger log = LoggerFactory.getLogger(ConsumerService.class);
    private final KafkaConsumerService kafkaConsumerService;

    public ConsumerService(KafkaConsumerService kafkaConsumerService) {
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @PostConstruct
    public void init() {
        log.info("Запуск ConsumerService");
        kafkaConsumerService.start();
    }

    @PreDestroy
    public void stop() {
        log.info("Остановка ConsumerService");
        kafkaConsumerService.stop();
    }
}
