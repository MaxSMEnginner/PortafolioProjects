package com.maxacm.lr.service;

import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("The user already exists");
        }
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role("ROLE_USER")
                .build();
        return userRepository.save(user);
    }

    public User registeradmin(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("The user already exists");
        }
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role("ROLE_ADMIN")
                .build();
        return userRepository.save(user);
    }
}
