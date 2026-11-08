package org.example.razvojsoftvera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RazvojSoftveraApplication {

	public static void main(String[] args) {
		SpringApplication.run(RazvojSoftveraApplication.class, args);
	}

}
