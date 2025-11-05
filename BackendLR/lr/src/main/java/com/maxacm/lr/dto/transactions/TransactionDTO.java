package com.maxacm.lr.dto.transactions;

import com.maxacm.lr.Enum.TypeTransactions.TypeTransaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
@Data
public class TransactionDTO {
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDateTime date;
    private TypeTransaction type;
    private Long userId;
    private Long accountId;
    private Long categoryId;
}
