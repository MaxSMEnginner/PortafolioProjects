package com.maxacm.lr.controller.user;

import com.maxacm.lr.dto.users.UserDTO;
import com.maxacm.lr.dto.users.UserRegister;
import com.maxacm.lr.dto.users.UserUpdateDTO;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.service.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.maxacm.lr.repository.users.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;


    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    // ✅ Crear usuario (usando UserService para encriptar password)
    @PostMapping("/register")
    public ResponseEntity<User> create(@RequestBody UserRegister user) {
        User saved = userService.register(user);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/home")
    public String home() {
        return "Bienvenido USER 🚀";
    }

    @PatchMapping("/user/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        try{
            User updateuser= userService.updateUSER(id, dto);
            return ResponseEntity.ok(userService.toDTO(updateuser));
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getuserByUsername(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            return ResponseEntity.ok(userService.toDTO(user));  // ← Convertir a DTO
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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

    //Es lo mismo a lo de arriba
//    @GetMapping("/users/username/{username}")
//    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
//        return userService.findByUsername(username)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }



}
