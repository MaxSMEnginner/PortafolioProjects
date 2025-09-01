package com.maxacm.lr.controller;


import com.maxacm.lr.dto.RefreshRequest;
import com.maxacm.lr.dto.RefreshResponse;
import com.maxacm.lr.dto.AuthResponse;
import com.maxacm.lr.dto.LoginRequest;
import com.maxacm.lr.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        try {
            if (!jwtUtil.validateToken(request.getRefreshToken()) || !jwtUtil.isRefreshToken(request.getRefreshToken())) {
                return ResponseEntity.status(403).body(new AuthResponse("Invalid or expired token"));
            }

            String username = jwtUtil.extractUsername(request.getRefreshToken());
            String newAccessToken = jwtUtil.generateAccessToken(username);
            String newRefreshToken = jwtUtil.generateRefreshToken(username);

            return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));

        } catch (Exception e) {
            return ResponseEntity.status(403).body(new AuthResponse("Invalid or expired token"));
        }
    }


    @GetMapping("/home")
    public String home() {
        return "Bienvenido al HOME protegido 🚀";
    }
}
