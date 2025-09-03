package com.maxsoft.loginregister.security;

import com.maxsoft.loginregister.component.FAJwt;
import com.maxsoft.loginregister.service.DetallesUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecuridadConfiguracion {
    private final FAJwt faJwt;
    private final DetallesUsuarioService servicio;





    @Bean
    public PasswordEncoder codificadorcontras(){

        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain cadenaFiltroSeguridad(HttpSecurity http) throws Exception{
        http.csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers("/users/register").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(faJwt, UsernamePasswordAuthenticationFilter.class);


        return http.build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(servicio);
        authProvider.setPasswordEncoder(codificadorcontras());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
