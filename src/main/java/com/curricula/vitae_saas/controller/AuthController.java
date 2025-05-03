package com.curricula.vitae_saas.controller;

import com.curricula.vitae_saas.dto.*;
import com.curricula.vitae_saas.model.AppUser;
import com.curricula.vitae_saas.model.RefreshToken;
import com.curricula.vitae_saas.service.AuthService;
import com.curricula.vitae_saas.service.JwtService;
import com.curricula.vitae_saas.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/signup")
    public ResponseEntity<AppUser> register(@RequestBody RegisterRequest registerUserDto) {
        AppUser registeredUser = authService.saveUser(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody AuthRequest loginUserDto) {
        AppUser authenticatedUser = authService.logIn(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = refreshTokenService.createRefreshToken(authenticatedUser);

        return ResponseEntity.ok(TokenResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationTime())
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyExpiration(request.getRefreshToken());
        String newAccessToken = jwtService.generateToken(refreshToken.getUser());

        return ResponseEntity.ok(TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .expiresIn(jwtService.getExpirationTime())
                .build());
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDTO verifyUserDto) {
        authService.verifyUser(verifyUserDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@RequestParam String email) {
        authService.resendVerificationCode(email);
        return ResponseEntity.ok().build();
    }

}