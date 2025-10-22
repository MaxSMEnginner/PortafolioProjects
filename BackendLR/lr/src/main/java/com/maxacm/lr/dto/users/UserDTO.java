package com.maxacm.lr.dto.users;

import lombok.Data;
import lombok.Builder;
import com.maxacm.lr.Enum.TypeUsers.Roles;
import java.time.LocalDateTime;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private Roles role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}