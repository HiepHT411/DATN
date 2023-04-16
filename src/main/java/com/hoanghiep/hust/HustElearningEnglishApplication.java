package com.hoanghiep.hust;

import com.hoanghiep.hust.service.IAudioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HustElearningEnglishApplication {

	public static void main(String[] args) {
		SpringApplication.run(HustElearningEnglishApplication.class, args);
	}

//	@Bean
//	CommandLineRunner init(IAudioService audioService) {
//		return (args) -> {
//			audioService.init();
//		};
//	}
}
