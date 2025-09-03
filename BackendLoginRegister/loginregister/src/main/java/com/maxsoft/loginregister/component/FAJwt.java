package com.maxsoft.loginregister.component;


import com.fasterxml.jackson.core.JacksonException;
import com.maxsoft.loginregister.service.DetallesUsuarioService;
import jakarta.servlet.FilterChain;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FAJwt extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final DetallesUsuarioService usuariodetalles;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String ruta=request.getServletPath();
        if((ruta.startsWith("/auth/login"))||(ruta.startsWith("/auth/refresh"))){
            filterChain.doFilter(request,response);
            return;
        }

        final String EncabezadoAutorizacion = request.getHeader("Authorization");
        String nombreusuario=null;
        String jwt= null;


        if(EncabezadoAutorizacion!=null && EncabezadoAutorizacion.startsWith("Bearer ")){
            jwt=EncabezadoAutorizacion.substring(7);

            try{
                Claims reclamo=jwtUtil.extraerTodasClaims(jwt);
                String tipo=reclamo.get("tipo",String.class);


                if(!("Acceso".equals(tipo))){
                    throw new JwtException("La token que ingreso no es del tipo 'Acceso'");
                }

                nombreusuario= reclamo.getSubject();

            } catch (JwtException e){
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Token: Invalida o Expirada");
                return;
            }
        }

        if(nombreusuario!=null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails detallesusuario = usuariodetalles.loadUserByUsername(nombreusuario);
            if(jwtUtil.ValidarToken(jwt)){
                UsernamePasswordAuthenticationToken tokenautentificado =
                        new UsernamePasswordAuthenticationToken(
                                detallesusuario,
                                null,
                                detallesusuario.getAuthorities()
                        );
                tokenautentificado.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(tokenautentificado);
            }
        }

        filterChain.doFilter(request,response);





    }
}
