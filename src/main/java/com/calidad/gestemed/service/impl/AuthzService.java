package com.calidad.gestemed.service.impl;


import com.calidad.gestemed.domain.RolePolicy;
import com.calidad.gestemed.repo.RolePolicyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/*
la función de este servicio es es verificar si un usuario con un rol específico tiene ciertos permisos
 */
@Service
@RequiredArgsConstructor
public class AuthzService {

    private final RolePolicyRepo repo;

    // devuelve verdadero si el usuario tiene permiso o falso si no tiene permiso
    public boolean has(String role, java.util.function.Predicate<RolePolicy> checker) {
        return repo.findByRoleName(role)
                .map(checker::test)
                .orElse(false);
    }


}