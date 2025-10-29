package com.funflare.funflare;

import com.funflare.funflare.config.MpesaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MpesaConfig.class)
public class FunflareApplication {

	public static void main(String[] args) {
		SpringApplication.run(FunflareApplication.class, args);
	}

}
