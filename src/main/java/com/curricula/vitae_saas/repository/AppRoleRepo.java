package com.curricula.vitae_saas.repository;

import com.curricula.vitae_saas.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepo extends JpaRepository<AppRole, Long> {
    Optional<AppRole> findByRoleName(String roleName);
}
