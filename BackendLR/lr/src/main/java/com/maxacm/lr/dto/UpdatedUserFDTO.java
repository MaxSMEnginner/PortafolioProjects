package com.maxacm.lr.dto;
import lombok.Data;
import com.maxacm.lr.Enum.Roles;

import java.util.Optional;

@Data
public class UpdatedUserFDTO {
    private String username;
    private String password;
    private Roles role;
}
