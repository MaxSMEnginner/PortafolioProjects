package com.maxacm.lr.dto;

//import com.maxacm.lr.entity.Category;
//import com.maxacm.lr.entity.Account;
import com.maxacm.lr.Enum.TypeAccount;
import lombok.Data;


import java.math.BigDecimal;

@Data
public class NewTransaction {
    private BigDecimal amount;
    private String description;
    private String accountName;
    private TypeAccount accountType;
    private String categoryName;
}
