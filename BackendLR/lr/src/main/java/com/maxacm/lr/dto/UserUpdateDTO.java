package com.maxacm.lr.dto;
import java.util.Optional;


public record UserUpdateDTO(Optional<String> username, Optional<String> password, Optional<String> role) {

}
