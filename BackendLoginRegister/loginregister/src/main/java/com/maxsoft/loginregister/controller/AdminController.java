package com.maxsoft.loginregister.controller;

import com.maxsoft.loginregister.entity.Registro;
import com.maxsoft.loginregister.entity.Usuario;
import com.maxsoft.loginregister.repository.UsuarioRepositorio;
import com.maxsoft.loginregister.service.ARegistroService;
import com.maxsoft.loginregister.entity.ListanegraToken;
import com.maxsoft.loginregister.service.LNTokenService;
import com.maxsoft.loginregister.service.UsuarioServicio;
import com.maxsoft.loginregister.dto.DTOModificarUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.maxsoft.loginregister.dto.DTOUsuario;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioRepositorio usuarioRepositorio;
    private final UsuarioServicio usuarioServicio;
    private final ARegistroService registroServicio;
    private final LNTokenService listanegraserivicio;

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @GetMapping("/verlntokens")
    public List<ListanegraToken> vertokens(){
        return listanegraserivicio.vertokens();
    }

    // ✅ Ver todos los logs
    @GetMapping("/audit-log")
    public List<Registro> getAll() {
        return registroServicio.obtenerTodos();
    }

    // ✅ Filtrar logs por usuario
    @GetMapping("/audit-log/user/{nombreusuario}")
    public List<Registro> getByUsuario(@PathVariable String nombreusuario) {
        return registroServicio.obtenerPorNombre(nombreusuario);
    }

    // ✅ Filtrar logs por acción (LOGIN, REFRESH, LOGOUT)
    @GetMapping("/audit-log/action/{accion}")
    public List<Registro> getByAction(@PathVariable String accion) {
        return registroServicio.obtenerPorAccion(accion);
    }

    // ✅ Home para ADMIN
    @GetMapping("/home")
    public String admin() {
        return "Bienvenido ADMIN 👑";
    }

    // ✅ Crear usuario ADMIN
    @PostMapping("/registeradmin")
    public ResponseEntity<Usuario> create(@RequestBody DTOUsuario usuario) {
        Usuario saved = usuarioServicio.registraradmin(usuario.getNombreusuario(), usuario.getContrasena());
        return ResponseEntity.ok(saved);
    }

    // ✅ Listar todos los usuarios
    @GetMapping("/users")
    public List<Usuario> findAll() {
        return usuarioRepositorio.findAll();
    }

    // ✅ Buscar usuario por ID
    @GetMapping("/user/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable Long id) {
        return usuarioRepositorio.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Actualizar usuario parcialmente
    @PatchMapping("/user/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Long id, @RequestBody DTOModificarUsuario dto) {
        return usuarioRepositorio.findById(id).map(user -> {
            dto.nombreusuario().ifPresent(user::setNombreusuario);
            dto.contrasena().ifPresent(p -> user.setContrasena(passwordEncoder().encode(p)));
            dto.rol().ifPresent(user::setRol);
            return ResponseEntity.ok(usuarioRepositorio.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Eliminar usuario
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (usuarioRepositorio.existsById(id)) {
            usuarioRepositorio.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
