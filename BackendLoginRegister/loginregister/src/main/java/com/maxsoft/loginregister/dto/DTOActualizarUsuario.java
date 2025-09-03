package com.maxsoft.loginregister.dto;
import java.util.Optional;


public record DTOActualizarUsuario(Optional<String> nombreusuario, Optional<String> contrasena, Optional<String> rol) {

}
