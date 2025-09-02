package com.maxsoft.loginregister.repository;

import com.maxsoft.loginregister.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByNombreusuario(String nombreusuario); //
    boolean existsByNombreusuario(String nombreusuario);
    Optional<Usuario> findById(Long id);
}