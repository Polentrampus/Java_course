package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.KafkaConfig;
import dto.TransferMessage;
import model.MoneyTransfer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class KafkaProducerService {
    private final KafkaProducer<String, TransferMessage> producer;
    private final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private final String topicName;

    public KafkaProducerService(KafkaProducer<String, TransferMessage> producer, String topicName) {
        this.producer = producer;
        this.topicName = topicName;
    }

    public void send(TransferMessage message) {
        try {
            String key = message.transferId().toString();
            ProducerRecord<String, TransferMessage> record = new ProducerRecord<>(topicName, key, message);
            producer.beginTransaction();
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get();
            producer.commitTransaction();
            log.debug("Сообщение отправлено в топик {}, партиция {}, offset {}",
                    metadata.topic(), metadata.partition(), metadata.offset());
        } catch (Exception e) {
            try {
                producer.abortTransaction();
            } catch (Exception abortEx) {
                log.error("Ошибка при откате Kafka-транзакции", abortEx);
            }
            log.error("Ошибка при отправке сообщения: {}", message.transferId());
            throw new RuntimeException("Ошибка отправки в Kafka", e);
        }
    }

    public void close() {
        producer.close();
    }
}