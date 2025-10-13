package com.maxacm.lr.dto;

import lombok.Data;
import com.maxacm.lr.Enum.TypeAccount;
import com.maxacm.lr.entity.Account;
import java.math.BigDecimal;

@Data
public class NewAccount {
    private String name;
    private BigDecimal saldoActual;
    private TypeAccount type;
    private Long userId;

}
