package com.maxsoft.loginregister.dto;

import java.util.Optional;

public record DTOModificarUsuario(Optional<String> nombreusuario, Optional<String> contrasena, Optional<String> rol) {
}
