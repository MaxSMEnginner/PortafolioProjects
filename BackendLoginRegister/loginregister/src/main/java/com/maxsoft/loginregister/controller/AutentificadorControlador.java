package com.maxsoft.loginregister.controller;

import com.maxsoft.loginregister.dto.DTONuevaTokenPeticion;
import com.maxsoft.loginregister.dto.DTOAutentificadorRespuesta;
import com.maxsoft.loginregister.dto.DTOIngresarPeticion;
import com.maxsoft.loginregister.component.JwtUtil;
import com.maxsoft.loginregister.service.ARegistroService;
import com.maxsoft.loginregister.service.LNTokenService;
import com.maxsoft.loginregister.service.NTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AutentificadorControlador {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ARegistroService registroservicio;
    private final LNTokenService Listanegraservicio;
    private final NTokenService nuevotokenservicio;

    @PostMapping("/login")
    public ResponseEntity<DTOAutentificadorRespuesta> ingresar(@RequestBody DTOIngresarPeticion detalles){

    }



}
