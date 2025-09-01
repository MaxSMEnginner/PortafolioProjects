package com.maxacm.lr.controller;

import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.UserRepository;
import com.maxacm.lr.service.UserService;
import com.maxacm.lr.dto.UserUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @GetMapping("/home")
    public String admin() {
        return "Bienvenido ADMIN 👑";
    }

    // ✅ Crear usuario (usando UserService para encriptar password)
    @PostMapping("/registeradmin")
    public ResponseEntity<User> create(@RequestBody User user) {
        User saved = userService.registeradmin(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(saved);
    }

    // ✅ Listar todos los usuarios
    @GetMapping("/users")
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // ✅ Buscar por ID
    @GetMapping("/user/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Actualizar usuario (solo username por ejemplo)
    @PatchMapping("/user/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        return userRepository.findById(id).map(user -> {

            dto.username().ifPresent(user :: setUsername);//dto.username().ifPresent(username -> user.setUsername(username));
            dto.password().ifPresent(p -> user.setPassword(passwordEncoder().encode(p)));
            dto.role().ifPresent(user::setRole);//dto.role().ifPresent(role -> user.setRole(role));

            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Eliminar usuario
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


}
