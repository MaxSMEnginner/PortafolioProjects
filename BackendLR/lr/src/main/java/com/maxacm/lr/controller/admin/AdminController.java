package com.maxacm.lr.controller.admin;

import com.maxacm.lr.dto.auditlog.AuditLogDTO;
import com.maxacm.lr.entity.AuditLog;
import com.maxacm.lr.dto.blacklisttoken.BlacklistedTokenDTO;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.exception.users.UserAlreadyExistsException;
import com.maxacm.lr.exception.users.UserNotFoundException;
import com.maxacm.lr.repository.users.UserRepository;
import com.maxacm.lr.service.auths.AuditLogService;
import com.maxacm.lr.service.users.UserService;
import com.maxacm.lr.service.auths.TokenBlacklistService;
import com.maxacm.lr.dto.users.UserUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.maxacm.lr.dto.users.UserRegister;
import com.maxacm.lr.dto.users.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final AuditLogService auditLogService;
    private final TokenBlacklistService blacklistService;

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @GetMapping("/black-list")
    public List<BlacklistedTokenDTO> getblAll(){
        return blacklistService.getAll();

    }

    // ✅ Ver todos los logs
    @GetMapping("/audit-log")
    public List<AuditLogDTO> getAll() {
        return auditLogService.getAll();
    }

    // ✅ Filtrar logs por usuario
    @GetMapping("/audit-log/user/{username}")
    public List<AuditLogDTO> getByUser(@PathVariable String username) {
        return auditLogService.getByUser(username);
    }

    // ✅ Filtrar logs por acción (LOGIN, REFRESH, LOGOUT)
    @GetMapping("/audit-log/action/{action}")
    public List<AuditLogDTO> getByAction(@PathVariable String action) {
        return auditLogService.getByAction(action);
    }

    // ✅ Home para ADMIN
    @GetMapping("/home")
    public String admin() {
        return "Bienvenido ADMIN 👑";
    }

    // ✅ Crear usuario ADMIN
    @PostMapping("/registeradmin")
    public ResponseEntity<User> create(@RequestBody UserRegister user) {
        User saved = userService.registeradmin(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/register")
    public ResponseEntity<User> createuser(@RequestBody UserRegister user) {
        User saved = userService.register(user);
        return ResponseEntity.ok(saved);
    }


    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // ✅ Buscar usuario por ID
    @GetMapping("/user/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Actualizar usuario parcialmente
    @PatchMapping("/user/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        try{
            User updateuser= userService.updateUSERadmin(id, dto);
            return ResponseEntity.ok(userService.toDTO(updateuser));
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: "+e.getMessage());
        }
    }

    // ✅ Eliminar usuario
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
