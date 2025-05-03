package com.curricula.vitae_saas.service;

import com.curricula.vitae_saas.dto.AuthRequest;
import com.curricula.vitae_saas.dto.RegisterRequest;
import com.curricula.vitae_saas.dto.VerifyUserDTO;
import com.curricula.vitae_saas.exception.*;
import com.curricula.vitae_saas.model.AppRole;
import com.curricula.vitae_saas.model.AppUser;
import com.curricula.vitae_saas.repository.AppRoleRepo;
import com.curricula.vitae_saas.repository.AppUserRepo;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final AppUserRepo appUserRepo;
    private final AppRoleRepo appRoleRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AppUser saveUser(RegisterRequest request) {
        appUserRepo.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new BusinessException("Username already exists");
        });
        appUserRepo.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new BusinessException("Email already exists");
        });
        AppRole userRole = appRoleRepo.findByRoleName("USER")
                .orElseGet(() -> {
                    AppRole newRole = new AppRole();
                    newRole.setRoleName("USER");
                    return appRoleRepo.save(newRole);
                });
        String verificationCode = generateVerificationCode();
        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(List.of(userRole))
                .enabled(false)
                .verificationCode(verificationCode)
                .verificationCodeExpiration(LocalDateTime.now().plusHours(1))
                .build();

        AppUser savedUser = appUserRepo.save(user);
        sendVerificationEmail(savedUser);
        return savedUser;
    }


    public AppUser logIn(AuthRequest input) {
        AppUser user = appUserRepo.findByEmail(input.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new AccountNotVerifiedException("Account not verified. Please verify your account.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );

            if (!authentication.isAuthenticated()) {
                throw new AuthenticationFailedException("Authentication failed");
            }

            return user;
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }
    public void verifyUser(VerifyUserDTO input) {
        AppUser user = appUserRepo.findByEmail(input.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new BusinessException("Account is already verified");
        }

        if (LocalDateTime.now().isAfter(user.getVerificationCodeExpiration())) {
            throw new BusinessException("Verification code has expired");
        }

        if (!user.getVerificationCode().equals(input.getVerificationCode())) {
            throw new BusinessException("Invalid verification code");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiration(null);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        appUserRepo.save(user);
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
    public AppUser getUser(String username) {
        return appUserRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }
    private void sendVerificationEmail(AppUser user) {
        String subject = "Please Verify Your Account";
        String htmlMessage = String.format("""
        <html>
            <body style="font-family: Arial, sans-serif;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2563eb;">Welcome to VitaeMaker!</h2>
                    <p>Your verification code is:</p>
                    <div style="font-size: 24px; font-weight: bold;
                        background-color: #f3f4f6; padding: 15px;
                        border-radius: 5px; text-align: center; margin: 20px 0;">
                        %s
                    </div>
                    <p>This code will expire in 1 hour.</p>
                </div>
            </body>
        </html>
        """, user.getVerificationCode());

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}", user.getEmail(), e);
            throw new EmailSendingException("Failed to send verification email");
        }
    }
    private void sendForgotPasswordEmail(AppUser user) {

    }
    public void resendVerificationCode(String email) {
        Optional<AppUser> optionalUser = appUserRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            if (user.isEnabled()) {
                throw new BusinessException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiration(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            appUserRepo.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }


    public List<AppUser> getUsers() {
        log.info("Fetching all users");
        return appUserRepo.findAll();
    }
}
