package com.maxsoft.loginregister.service;
import com.maxsoft.loginregister.entity.Usuario;
import com.maxsoft.loginregister.repository.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

@Service
@RequiredArgsConstructor
public class DetallesUsuarioService implements UserDetailsService {
    private final UsuarioRepositorio repo;

    @Override
    public UserDetails loadUserByUsername(String nombreusuario) throws UsernameNotFoundException {
        Usuario usuario =repo.findByNombreusuario(nombreusuario)
                .orElseThrow(()->new UsernameNotFoundException("No se encontro el nombre del usuario: "+ nombreusuario));

        return User.builder()
                .username(usuario.getNombreusuario())
                .password(usuario.getContrasena())
                .roles(usuario.getRol().replace("ROL_",""))
                .build();
    }



}
