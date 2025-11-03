package com.maxacm.lr.security.Jwt;

import com.maxacm.lr.security.CustomService.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("\n🔍 === REQUEST DEBUG ===");
        System.out.println("Method: " + request.getMethod());
        System.out.println("Path: " + path);

        // Saltar validación en login y refresh
        if (path.startsWith("/auth/login") || path.startsWith("/auth/refresh")) {
            System.out.println("✅ Public endpoint, skipping auth");
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + (authHeader != null ? "Present" : "Missing"));

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("Token extracted (first 20 chars): " + jwt.substring(0, Math.min(20, jwt.length())) + "...");

            try {
                Claims claims = jwtUtil.extractAllClaims(jwt);
                String type = claims.get("type", String.class);
                System.out.println("Token type: " + type);

                if (!"ACCESS".equals(type)) {
                    System.out.println("❌ Invalid token type");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Invalid token type: only ACCESS tokens allowed here");
                    return;
                }

                username = claims.getSubject();
                System.out.println("Username from token: " + username);
                System.out.println("Roles in token: " + claims.get("roles"));

            } catch (JwtException e) {
                System.out.println("❌ JWT Exception: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        } else {
            System.out.println("⚠️ No Bearer token found");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("🔐 Loading user details for: " + username);

            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("User loaded successfully");
                System.out.println("User authorities: " + userDetails.getAuthorities());

                if (jwtUtil.validateToken(jwt)) {
                    System.out.println("✅ Token is valid");

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("✅ Authentication set in SecurityContext");
                    System.out.println("Authorities in context: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                } else {
                    System.out.println("❌ Token validation failed");
                }
            } catch (Exception e) {
                System.out.println("❌ Error loading user: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ Username is null or user already authenticated");
        }

        System.out.println("=== END REQUEST DEBUG ===\n");
        filterChain.doFilter(request, response);
    }
}