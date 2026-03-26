package service;

import dto.TransferMessage;
import model.Account;
import model.MoneyTransfer;
import model.TransferStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransferProcessor {
    private final SessionFactory sessionFactory;
    private final AccountService accountService;
    private final Logger log = LoggerFactory.getLogger(TransferProcessor.class);

    public TransferProcessor(SessionFactory sessionFactory, AccountService accountService) {
        this.sessionFactory = sessionFactory;
        this.accountService = accountService;
    }

    public void process(TransferMessage message) {
        log.info("Начало обработки сообщения: transferId={}", message.transferId());

        try (Session session = sessionFactory.openSession()) {
            Account fromAccount = accountService.findAccountById(session, message.fromAccountId());
            Account toAccount = accountService.findAccountById(session, message.toAccountId());

            if (fromAccount == null || toAccount == null) {
                log.error("Ошибка валидации: счет не найден. fromId={}, toId={}",
                        message.fromAccountId(), message.toAccountId());
                return;
            }

            if (fromAccount.getBalance().compareTo(message.sum()) <= 0) {
                log.error("Ошибка валидации: недостаточно средств. accountId={}, balance={}, amount={}",
                        message.fromAccountId(), fromAccount.getBalance(), message.sum());
                saveTransferWithError(message);
                return;
            }

            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                fromAccount.setBalance(fromAccount.getBalance().subtract(message.sum()));
                toAccount.setBalance(toAccount.getBalance().add(message.sum()));
                session.merge(fromAccount);
                session.merge(toAccount);

                MoneyTransfer transfer = new MoneyTransfer(
                        message.transferId(),
                        message.fromAccountId(),
                        message.toAccountId(),
                        message.sum(),
                        TransferStatus.SUCCESS.toString()
                );
                session.persist(transfer);

                tx.commit();
                log.info("Успешная обработка сообщения: transferId={}", message.transferId());
            } catch (Exception e) {
                safeRollback(tx, message.transferId());
                log.error("Ошибка транзакции при обработке сообщения: transferId={}", message.transferId(), e);
                saveTransferWithError(message);
            }
        } catch (Exception e) {
            log.error("Критическая ошибка обработки сообщения: transferId={}", message.transferId(), e);
            saveTransferWithError(message);
        }
    }

    private void safeRollback(Transaction tx, java.util.UUID transferId) {
        if (tx == null) {
            return;
        }
        try {
            tx.rollback();
        } catch (Exception rollbackException) {
            log.error("Ошибка rollback транзакции: transferId={}", transferId, rollbackException);
        }
    }

    private void saveTransferWithError(TransferMessage message) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            MoneyTransfer transfer = new MoneyTransfer(
                    message.transferId(),
                    message.fromAccountId(),
                    message.toAccountId(),
                    message.sum(),
                    TransferStatus.FAILED.toString()
            );
            session.persist(transfer);
            tx.commit();
        } catch (Exception e) {
            log.error("Не удалось сохранить перевод с ошибкой: transferId={}", message.transferId(), e);
        }
    }
}