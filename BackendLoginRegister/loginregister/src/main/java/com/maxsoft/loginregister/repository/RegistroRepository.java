package com.maxsoft.loginregister.repository;

import com.maxsoft.loginregister.entity.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegistroRepository extends JpaRepository<Registro, Long> {

    List<Registro>findByNombreusuario(String nombreusuario);
    List<Registro> findByAccion(String accion);
}
