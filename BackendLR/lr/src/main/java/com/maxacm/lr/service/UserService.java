package com.maxacm.lr.service;

import com.maxacm.lr.dto.UserUpdateDTO;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

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


    public User updateUSER(Long id, UserUpdateDTO dto){
        return userRepository.findById(id).map(user -> {

            dto.username().ifPresent(user::setUsername);
            dto.password().ifPresent(p -> user.setPassword(passwordEncoder().encode(p)));
            return userRepository.save(user);

        }).orElseThrow(() -> new RuntimeException("Not Found User"));

    }


    public User updateUSERadmin(Long id, UserUpdateDTO dto){
        return userRepository.findById(id).map(user -> {

            dto.username().ifPresent(user::setUsername);
            dto.password().ifPresent(p -> user.setPassword(passwordEncoder().encode(p)));
            dto.role().ifPresent(user::setRole);
            return userRepository.save(user);

        }).orElseThrow(() -> new RuntimeException("Not Found User"));

    }


    public User findByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("Not found User"));
    }
}
