package com.maxacm.lr.dto;
import lombok.Data;

import java.util.Optional;

@Data
public class UpdatedUserFDTO {
    private String username;
    private String password;
    private String role;
}
