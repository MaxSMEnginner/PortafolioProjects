package com.maxsoft.loginregister.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOAutentificadorRespuesta {
    private String tokenAcceso;
    private String tokenNT;

    // Constructor solo para error/mensaje (1 parámetro → no choca)
    public DTOAutentificadorRespuesta(String message) {
        this.tokenAcceso= message;
        this.tokenNT = null;
    }
}
