package com.curricula.vitae_saas.controller;

import com.curricula.vitae_saas.model.AppRole;
import com.curricula.vitae_saas.model.AppUser;
import com.curricula.vitae_saas.service.AppUserService;
import com.curricula.vitae_saas.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppUserController {
    private final AppUserService appUserService;
    private final AuthService authService;

    @GetMapping("/public/users")
    public ResponseEntity<List<AppUser>> getAllUser() {
        return ResponseEntity.ok(authService.getUsers());
    }

    @GetMapping("/admin/roles")
    public ResponseEntity<List<AppRole>> getAllRoles() {
        return ResponseEntity.ok(appUserService.getRoles());
    }

    @GetMapping("/user/profile")
    public ResponseEntity<AppUser> getUserByUsername(@RequestParam String username) {
        return ResponseEntity.ok(authService.getUser(username));
    }

    @PostMapping("/admin/roles/create")
    public ResponseEntity<AppRole> addRole(@RequestBody AppRole role) {
        AppRole roleDetail = appUserService.saveRole(role);
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/admin/roles/create")
                .buildAndExpand()
                .toUriString());
        return ResponseEntity.created(uri).body(roleDetail);
    }

    @PostMapping("/admin/roles/assign")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm request) {
        appUserService.addRoleToUser(request.getUsername(), request.getRoleName());
        return ResponseEntity.ok("Successfully added role to user");
    }

    @Data
    private static class RoleToUserForm {
        private String username;
        private String roleName;
    }
}