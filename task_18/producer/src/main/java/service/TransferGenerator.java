package service;

import dto.TransferMessage;
import model.Account;
import model.TransferStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TransferGenerator {
    private final Logger log = LoggerFactory.getLogger(TransferGenerator.class);
    private final KafkaProducerService kafkaProducerService;
    private final Random random = new Random();

    public TransferGenerator(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    public void generateAndSend(Map<Long, Account> accountsMap) {
        List<Long> accountIds = new ArrayList<>(accountsMap.keySet());

        if (accountIds.size() < 2) {
            log.warn("Недостаточно счетов для генерации перевода");
            return;
        }

        Long fromAccountId = getRandomAccountId(accountIds);
        Long toAccountId = getRandomAccountId(accountIds);

        while (fromAccountId.equals(toAccountId)) {
            toAccountId = getRandomAccountId(accountIds);
        }

        UUID transferId = UUID.randomUUID();
        BigDecimal sum = BigDecimal.valueOf(Math.round((100 + random.nextDouble() * 900) * 100.0) / 100.0);

        TransferMessage message = new TransferMessage(
                transferId,
                fromAccountId,
                toAccountId,
                sum,
                TransferStatus.SUCCESS.toString()
        );

        kafkaProducerService.send(message);
        log.info("Отправлено сообщение: transferId={}, from={}, to={}, sum={}",
                transferId, fromAccountId, toAccountId, sum);
    }

    private Long getRandomAccountId(List<Long> accountIds) {
        return accountIds.get(random.nextInt(accountIds.size()));
    }

    public void close() {
        kafkaProducerService.close();
    }
}