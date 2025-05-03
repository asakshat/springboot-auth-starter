package com.curricula.vitae_saas;

import com.curricula.vitae_saas.model.AppRole;
import com.curricula.vitae_saas.model.AppUser;
import com.curricula.vitae_saas.service.AppUserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.ArrayList;

@SpringBootApplication
public class VitaeSaasApplication {

	public static void main(String[] args) {
		SpringApplication.run(VitaeSaasApplication.class, args);
	}
	Dotenv dotenv = Dotenv.load();
	@Bean
	CommandLineRunner run(AppUserService appUserService) {
		return args -> {
			appUserService.saveRole(new AppRole(null, "USER"));
			appUserService.saveRole(new AppRole(null, "ADMIN"));

		};
	}
}
