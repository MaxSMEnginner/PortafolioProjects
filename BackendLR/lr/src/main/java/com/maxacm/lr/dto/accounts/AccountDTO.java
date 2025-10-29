package com.maxacm.lr.dto.accounts;

import com.maxacm.lr.Enum.TypeAccounts.TypeAccount;
import com.maxacm.lr.entity.User;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class AccountDTO {
    private Long id;
    private BigDecimal currentBalance;
    private String name;
    private TypeAccount type;
    private User user;
}
