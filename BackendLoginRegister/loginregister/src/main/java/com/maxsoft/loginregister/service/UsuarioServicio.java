package com.maxsoft.loginregister.service;
import com.maxsoft.loginregister.entity.Usuario;
import com.maxsoft.loginregister.repository.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder codificadordecontras;


    public Usuario registrar(String nombreusuario, String contra){
        if(usuarioRepositorio.existsByNombreusuario(nombreusuario)){
            throw new RuntimeException("El usuario ya existe");
        }

        Usuario usuario = Usuario.builder()
                .nombreusuario(nombreusuario)
                .contrasena(codificadordecontras.encode(contra))
                .rol("ROL_USER")
                .build();

        return usuarioRepositorio.save(usuario);
    }

    public Usuario registraradmin(String nombreusuario, String contra){
        if(usuarioRepositorio.existsByNombreusuario(nombreusuario)){
            throw new RuntimeException("El usuario ya existe");
        }

        Usuario usuario = Usuario.builder()
                .nombreusuario(nombreusuario)
                .contrasena(codificadordecontras.encode(contra))
                .rol("ROL_ADMIN")
                .build();


        return usuarioRepositorio.save(usuario);
    }


}
