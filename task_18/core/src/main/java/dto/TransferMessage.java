package dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferMessage(
        UUID transferId,
        Long fromAccountId,
        Long toAccountId,
        BigDecimal sum,
        String status
) implements Serializable {
}
