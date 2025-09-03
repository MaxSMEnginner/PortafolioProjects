package com.maxsoft.loginregister.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DTOUsuarioRegistro {
    @NotBlank(message="El Nombre de usuario no puede estar vacio")
    @Size(min=4, max=30)
    private String nombreusuario;

    @NotBlank(message="La contrasena no puede estar vacio")
    @Size(min=4, max=30)
    private String contrasena;

}
