package com.maxacm.lr.dto.users;
import lombok.Data;
import com.maxacm.lr.Enum.TypeUsers.Roles;

@Data
public class UpdatedUserFDTO {
    private String username;
    private String password;
    private Roles role;
}
