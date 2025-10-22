package com.maxacm.lr.controller.auth;

import com.maxacm.lr.dto.auths.AuthResponse;
import com.maxacm.lr.dto.login.LoginRequest;
import com.maxacm.lr.dto.auths.RefreshRequest;
import com.maxacm.lr.dto.users.UpdatedUserFDTO;
import com.maxacm.lr.security.Jwt.JwtUtil;

import com.maxacm.lr.service.auths.AuditLogService;
import com.maxacm.lr.service.auths.RefreshTokenService;
import com.maxacm.lr.service.auths.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.maxacm.lr.security.CustomService.CustomUserDetailsService;

import java.time.LocalDateTime;


//import com.maxacm.lr.service.LoginService;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuditLogService auditLogService;
    private final RefreshTokenService refreshTokenService;
//    private final LoginService loginService;


//    @PostMapping("/login2")
//    public ResponseEntity<ArrayList<String>> login2(String username, String password){
//        return ResponseEntity.ok(loginService.login(username, password));
//    }

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
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        refreshTokenService.create(userDetails.getUsername(), refreshToken,
                LocalDateTime.now().plusDays(7));

        auditLogService.log(userDetails.getUsername(), "LOGIN", http.getRemoteAddr());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/newjwtwcmu")
    public ResponseEntity<AuthResponse> newtoken(@RequestBody UpdatedUserFDTO request,
                                              HttpServletRequest http) {

        log.info("🔑 New JWT because you change yourself username or role: {}", request.getUsername());

        String accessToken = jwtUtil.Newjwt(request.getUsername(),request.getRole().toString());
        String refreshToken = jwtUtil.NewRT(request.getUsername(), request.getRole().toString());
        log.info("Access Token: {}, Refresh Token: {}", accessToken, refreshToken);

        refreshTokenService.create(request.getUsername(), refreshToken,
                LocalDateTime.now().plusDays(7));

        auditLogService.log(request.getUsername(), "MODIFY", http.getRemoteAddr());
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

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            refreshTokenService.invalidate(request.getRefreshToken());

            String newAccessToken = jwtUtil.generateAccessToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            refreshTokenService.create(userDetails.getUsername(), newRefreshToken, LocalDateTime.now().plusDays(7));

            auditLogService.log(userDetails.getUsername(), "REFRESH", http.getRemoteAddr());

            return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(new AuthResponse("Invalid or expired token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String header,
                                         HttpServletRequest http) {
        String token = header.substring(7);//Barear sdfdsfsdfsdfsd
        String username = jwtUtil.extractUsername(token);

        tokenBlacklistService.blacklistToken(token);
        auditLogService.log(username, "LOGOUT", http.getRemoteAddr());

        return ResponseEntity.ok("Logged out successfully");
    }
}
