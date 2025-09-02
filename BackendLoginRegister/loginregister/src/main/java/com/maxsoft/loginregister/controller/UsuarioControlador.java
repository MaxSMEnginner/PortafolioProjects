package com.maxsoft.loginregister.controller;

import com.maxsoft.loginregister.entity.Usuario;
import com.maxsoft.loginregister.service.UsuarioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UsuarioControlador {

    private final UsuarioServicio servicio;



    @PostMapping("/register")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario){
        Usuario guardar= servicio.registrar(usuario.getNombreusuario(), usuario.getContrasena());

        return ResponseEntity.ok(guardar);
    }







}
