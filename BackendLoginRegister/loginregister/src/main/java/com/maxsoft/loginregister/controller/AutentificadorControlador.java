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
import org.springframework.cglib.core.Local;
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
    private final LNTokenService listanegraservicio;
    private final NTokenService nuevotokenservicio;

    @PostMapping("/login")
    public ResponseEntity<DTOAutentificadorRespuesta> ingresar(@RequestBody DTOIngresarPeticion detalles, HttpServletRequest http){
        log.info("Intento de inicio de sesión para el usuario: "+ detalles.getNombreusuario());

        Authentication autenticador = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    detalles.getNombreusuario(),
                    detalles.getContrasena()
            )
        );

        UserDetails detallesusuario = (UserDetails) autenticador.getPrincipal();
        String TokenAcceso=jwtUtil.JwtAcceso(detalles.getNombreusuario());
        String TokenNT= jwtUtil.JwtNT(detalles.getNombreusuario());


        nuevotokenservicio.crear(detalles.getNombreusuario(), TokenNT, LocalDateTime.now().plusDays(7));

        registroservicio.registro(detalles.getNombreusuario(), "INGRESO", http.getRemoteAddr());

        return ResponseEntity.ok(new DTOAutentificadorRespuesta(TokenAcceso,TokenNT));
    }


    @PostMapping("/logout")
    public ResponseEntity<String> salir(@RequestHeader("Authorization") String bareartoken
    ,HttpServletRequest http){


        String tokenAcceso =  bareartoken.substring(7);
        String nombreusuario =  jwtUtil.extraerNombreusuario(tokenAcceso);

        listanegraservicio.AgregarTokenLN(tokenAcceso);
        registroservicio.registro(nombreusuario, "SALIR", http.getRemoteAddr());
        nuevotokenservicio.eliminarporusuario(nombreusuario);
        return ResponseEntity.ok("Salio exitosamente el usuario: "+nombreusuario);


    }

    @PostMapping("/refresh")
    public ResponseEntity<DTOAutentificadorRespuesta> recargartoken(@RequestBody DTONuevaTokenPeticion peticion ,
                                                                    HttpServletRequest http){
        try{
            if(!jwtUtil.ValidarToken(peticion.getTokenNT()) ||
                    !jwtUtil.EsNToken(peticion.getTokenNT())
                    || nuevotokenservicio.validar(peticion.getTokenNT())
            ){
                return ResponseEntity.status(403).body(new DTOAutentificadorRespuesta("Token Invalido o expirado"));
            }
            String nombreusuario =jwtUtil.extraerNombreusuario(peticion.getTokenNT());

            nuevotokenservicio.eliminar(peticion.getTokenNT());

            String TokenAcceso= jwtUtil.JwtAcceso(nombreusuario);
            String TokenNT= jwtUtil.JwtNT(nombreusuario);

            nuevotokenservicio.crear(nombreusuario,TokenNT,LocalDateTime.now().plusDays(7));

            registroservicio.registro(nombreusuario,"NUEVA TOKEN", http.getRemoteAddr());


            return ResponseEntity.ok(new DTOAutentificadorRespuesta(TokenAcceso,TokenNT));

        } catch (Exception e) {
            return ResponseEntity.status(403).body(new DTOAutentificadorRespuesta("Error Token"));
        }

    }



}
