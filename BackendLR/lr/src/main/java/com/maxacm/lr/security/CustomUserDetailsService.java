package com.maxacm.lr.security;

import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.maxacm.lr.Enum.Roles;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // --- LOG DE DEPURACIÓN 1 ---
        log.info("[DEBUG] Iniciando búsqueda para el usuario: '{}'", username);

        User userFromDb = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    // --- LOG DE DEPURACIÓN 2 ---
                    log.error("[DEBUG] ¡ERROR! No se encontró al usuario '{}' en la base de datos.", username);
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });

        // --- LOG DE DEPURACIÓN 3 ---
        log.info("[DEBUG] Usuario encontrado en la BD: Username={}, Rol={}", userFromDb.getUsername(), userFromDb.getRole());

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(userFromDb.getUsername())
                .password(userFromDb.getPassword())
                .roles(userFromDb.getRole().toString().replace("ROLE_", ""))
                .build();

        // --- LOG DE DEPURACIÓN 4 ---
        log.info("[DEBUG] Creado objeto UserDetails: Habilitado={}, Bloqueado={}", userDetails.isEnabled(), !userDetails.isAccountNonLocked());

        return userDetails;
    }
}
