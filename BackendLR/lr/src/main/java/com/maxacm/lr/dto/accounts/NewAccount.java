package com.maxacm.lr.dto.accounts;

import lombok.Data;
import com.maxacm.lr.Enum.TypeAccounts.TypeAccount;

import java.math.BigDecimal;

@Data
public class NewAccount {
    private String name;
    private BigDecimal currentBalance;
    private TypeAccount type;


}
