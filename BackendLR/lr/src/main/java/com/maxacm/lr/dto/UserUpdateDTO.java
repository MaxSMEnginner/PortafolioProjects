package com.maxacm.lr.dto;
import java.util.Optional;
import com.maxacm.lr.Enum.Roles;

public record UserUpdateDTO(Optional<String> username, Optional<String> password, Optional<Roles> role) {

}
