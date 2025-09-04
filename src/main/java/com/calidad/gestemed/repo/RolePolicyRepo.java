package com.calidad.gestemed.repo;

import com.calidad.gestemed.domain.RolePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolePolicyRepo extends JpaRepository<RolePolicy, Long> {


    //El Optional es un contenedor que puede o no tener un valor. Esto es bastante útil porque asi se evita el famoso error de NullPointerException.
    Optional<RolePolicy> findByRoleName(String roleName);
}
