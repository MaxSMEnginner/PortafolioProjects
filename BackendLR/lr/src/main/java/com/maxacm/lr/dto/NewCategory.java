package com.maxacm.lr.dto;
import com.maxacm.lr.Enum.TypeTransaction;
import lombok.Data;

@Data

public class NewCategory {
    private String name;
    private TypeTransaction type;
}
