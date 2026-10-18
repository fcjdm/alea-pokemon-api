package com.alea.pokemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Main entry point for the Alea Pokémon API Spring Boot application.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class AleaPokemonApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AleaPokemonApiApplication.class, args);
	}

}
