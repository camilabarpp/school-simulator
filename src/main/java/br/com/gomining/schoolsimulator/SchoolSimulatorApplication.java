package br.com.gomining.schoolsimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SchoolSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolSimulatorApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("123456"));
	}

}
