package com.maxsoft.loginregister.service;


import com.maxsoft.loginregister.entity.TokenNueva;
import com.maxsoft.loginregister.repository.NTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NTokenService {
    private final NTokenRepository repo;

    public TokenNueva crear(String nombreusuario, String token, LocalDateTime fechaexpiracion){
        return repo.save(new TokenNueva(null, token, nombreusuario, LocalDateTime.now()));
    }

    public boolean validar(String token){
        return repo.findByToken(token)
                .filter(nt-> nt.getFechaexpiracion().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public void eliminar(String token){
        repo.deleteByToken(token);
    }

    public void eliminarporusuario(String nombreusuario){
        repo.deleteByNombreusuario(nombreusuario);
    }

    @Scheduled(cron = "0 0 * * * *")// cada hora(segundo minuto hora dia mes diadelasemana)
    public void limpiartoken(){
        repo.findAll().stream()
                .filter(nt-> nt.getFechaexpiracion().isBefore(LocalDateTime.now()))
                .forEach(nt-> repo.delete(nt));
    }



}
