package com.maxacm.lr.dto.users;
import java.util.Optional;
import com.maxacm.lr.Enum.TypeUsers.Roles;

public record UserUpdateDTO(Optional<String> username, Optional<String> password, Optional<Roles> role) {

}
