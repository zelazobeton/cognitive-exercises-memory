package com.zelazobeton.cognitiveexercisesmemory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:caching.xml")
public class CognitiveExercisesMemoryApplication {
	public static void main(String[] args) {
		SpringApplication.run(CognitiveExercisesMemoryApplication.class, args);
	}
}
