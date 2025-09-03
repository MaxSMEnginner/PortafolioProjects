package com.maxsoft.loginregister.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DTONuevaTokenRespuesta {
    private String tokenAcceso;
    private String tokenNT;
}
