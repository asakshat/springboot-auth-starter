package com.curricula.vitae_saas.service;

import com.curricula.vitae_saas.model.AppUser;
import com.curricula.vitae_saas.model.RefreshToken;
import com.curricula.vitae_saas.repository.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final JwtService jwtService;

    public String createRefreshToken(AppUser user) {
        String token = jwtService.generateRefreshToken(user);

        Optional<RefreshToken> existingTokenOpt = refreshTokenRepo.findByUser_Id(user.getId());

        RefreshToken refreshToken;
        if (existingTokenOpt.isPresent()) {
            refreshToken = existingTokenOpt.get();
            refreshToken.setToken(token);
            refreshToken.setExpiration(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpirationTime() / 1000));
        } else {
            refreshToken = RefreshToken.builder()
                    .token(token)
                    .user(user)
                    .expiration(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpirationTime() / 1000))
                    .build();
        }

        refreshTokenRepo.save(refreshToken);
        return token;
    }


    public RefreshToken verifyExpiration(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiration().isBefore(LocalDateTime.now())) {
            refreshTokenRepo.delete(refreshToken);
            throw new RuntimeException("Refresh token was expired. Please make a new login request");
        }
        return refreshToken;
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepo.deleteByUser_Id(userId);
    }
}