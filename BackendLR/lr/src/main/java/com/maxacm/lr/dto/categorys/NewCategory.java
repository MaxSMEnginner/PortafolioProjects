package com.maxacm.lr.dto.categorys;
import com.maxacm.lr.Enum.TypeTransactions.TypeTransaction;
import lombok.Data;

@Data

public class NewCategory {
    private String name;
    private TypeTransaction type;
}
