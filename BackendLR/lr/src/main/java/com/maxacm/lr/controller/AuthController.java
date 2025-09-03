package com.maxacm.lr.controller;

import com.maxacm.lr.dto.AuthResponse;
import com.maxacm.lr.dto.LoginRequest;
import com.maxacm.lr.dto.RefreshRequest;
import com.maxacm.lr.security.JwtUtil;
import com.maxacm.lr.service.AuditLogService;
import com.maxacm.lr.service.RefreshTokenService;
import com.maxacm.lr.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuditLogService auditLogService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request,
                                              HttpServletRequest http) {
        log.info("🔑 Login attempt for user: {}", request.getUsername());

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        refreshTokenService.create(userDetails.getUsername(), refreshToken,
                LocalDateTime.now().plusDays(7));

        auditLogService.log(userDetails.getUsername(), "LOGIN", http.getRemoteAddr());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request,
                                                HttpServletRequest http) {
        try {
            if (!jwtUtil.validateToken(request.getRefreshToken()) ||
                    !jwtUtil.isRefreshToken(request.getRefreshToken()) ||
                    !refreshTokenService.validate(request.getRefreshToken())) {
                return ResponseEntity.status(403).body(new AuthResponse("Invalid or expired token"));
            }

            String username = jwtUtil.extractUsername(request.getRefreshToken());

            refreshTokenService.invalidate(request.getRefreshToken());

            String newAccessToken = jwtUtil.generateAccessToken(username);
            String newRefreshToken = jwtUtil.generateRefreshToken(username);
            refreshTokenService.create(username, newRefreshToken, LocalDateTime.now().plusDays(7));

            auditLogService.log(username, "REFRESH", http.getRemoteAddr());

            return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(new AuthResponse("Invalid or expired token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String header,
                                         HttpServletRequest http) {
        String token = header.substring(7);
        String username = jwtUtil.extractUsername(token);

        tokenBlacklistService.blacklistToken(token);
        auditLogService.log(username, "LOGOUT", http.getRemoteAddr());

        return ResponseEntity.ok("Logged out successfully");
    }
}
