package com.maxacm.lr.service.users;

import com.maxacm.lr.dto.users.UserDTO;
import com.maxacm.lr.dto.users.UserUpdateDTO;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.maxacm.lr.Enum.TypeUsers.Roles;
import com.maxacm.lr.dto.users.UserRegister;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // ✅ Método helper para convertir User a UserDTO
    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


    public User register(UserRegister userRegister) {
        if (userRepository.existsByUsername(userRegister.getUsername())) {
            throw new RuntimeException("The user already exists");
        }
        User user = User.builder()
                .username(userRegister.getUsername())
                .password(passwordEncoder.encode(userRegister.getPassword()))
                .role(Roles.ROLE_USER)
                .build();
        return userRepository.save(user);
    }

    public User registeradmin(UserRegister userRegister) {
        if (userRepository.existsByUsername(userRegister.getUsername())) {
            throw new RuntimeException("The user already exists");
        }
        User user = User.builder()
                .username(userRegister.getUsername())
                .password(passwordEncoder.encode(userRegister.getPassword()))
                .role(Roles.ROLE_ADMIN)
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

        }).orElseThrow(() -> new RuntimeException("Not Found AdminUser"));

    }


    public User findByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("Not found User"));
    }
}
