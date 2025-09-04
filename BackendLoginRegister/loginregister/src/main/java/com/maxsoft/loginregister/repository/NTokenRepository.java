package com.maxsoft.loginregister.repository;

import com.maxsoft.loginregister.entity.TokenNueva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NTokenRepository extends JpaRepository<TokenNueva, Long> {
    Optional<TokenNueva> findByToken(String token);
    void deleteByToken(String token);
    void deleteByNombreusuario(String nombreusuario);
}
