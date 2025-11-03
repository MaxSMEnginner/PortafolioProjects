package com.maxacm.lr.dto.categorys;

import com.maxacm.lr.Enum.TypeTransactions.TypeTransaction;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private TypeTransaction type;
    private Long userId;
}
