package com.curricula.vitae_saas.service;


import com.curricula.vitae_saas.exception.BusinessException;
import com.curricula.vitae_saas.exception.ResourceNotFoundException;
import com.curricula.vitae_saas.model.AppRole;
import com.curricula.vitae_saas.model.AppUser;
import com.curricula.vitae_saas.repository.AppRoleRepo;
import com.curricula.vitae_saas.repository.AppUserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppUserService {

    private final AppUserRepo appUserRepo;
    private final AppRoleRepo appRoleRepo;

    public AppRole saveRole(AppRole role) {
        String normalizedRoleName = role.getRoleName().toUpperCase();

        appRoleRepo.findByRoleName(normalizedRoleName).ifPresent(r -> {
            log.error("Role already exists: {}", r.getRoleName());
            throw new BusinessException("Role '" + r.getRoleName() + "' already exists");
        });

        role.setRoleName(normalizedRoleName);
        log.info("Saving new role: {}", normalizedRoleName);
        return appRoleRepo.save(role);
    }

    public void addRoleToUser(String username, String roleName) {
        AppUser user = appUserRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new ResourceNotFoundException("User not found");
                });

        AppRole role = appRoleRepo.findByRoleName(roleName)
                .orElseThrow(() -> {
                    log.error("Role not found: {}", roleName);
                    return new ResourceNotFoundException("Role not found");
                });

        if (user.getRoles().contains(role)) {
            log.error("User {} already has role {}", username, roleName);
            throw new BusinessException("User already has this role");
        }

        log.info("Adding role {} to user {}", roleName, username);
        user.getRoles().add(role);
    }


    public List<AppRole> getRoles() {
        log.info("Fetching all roles");
        return appRoleRepo.findAll();
    }
}