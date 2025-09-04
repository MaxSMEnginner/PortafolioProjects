package com.maxsoft.loginregister.service;

import com.maxsoft.loginregister.entity.Registro;
import com.maxsoft.loginregister.repository.RegistroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ARegistroService {
    private final RegistroRepository repo;


    public void registro(String nombreusuario, String accion, String ip){
        Registro registro = Registro.builder()
                .nombreusuario(nombreusuario)
                .accion(accion)
                .ip(ip)
                .fecharegistro(LocalDateTime.now())
                .build();

        repo.save(registro);
    }

    public List<Registro> obtenerTodos(){
        return repo.findAll();
    }

    public List<Registro> obtenerPorNombre(String nombreusuario){
        return repo.findByNombreusuario(nombreusuario);
    }

    public List<Registro> obtenerPorAccion(String accion){
        return repo.findByAccion(accion);
    }

}
