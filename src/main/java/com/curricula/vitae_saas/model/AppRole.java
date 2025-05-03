package com.curricula.vitae_saas.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppRole implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false )
    private String roleName;

    @PrePersist
    @PreUpdate
    private void normalizeRoleName() {
        this.roleName = this.roleName.toUpperCase().trim();
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + roleName;
    }
}