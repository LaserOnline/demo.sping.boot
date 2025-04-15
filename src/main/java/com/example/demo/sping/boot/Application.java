package com.example.demo.sping.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.demo.sping.boot.config.Config;

@SpringBootApplication
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		Config config = context.getBean(Config.class);
		String baseUrl = config.getBaseUrl();
		String javaVersion = System.getProperty("java.version");
    	System.out.println("☕ Running with Java version: " + javaVersion);
		System.out.println("🚀 Base URL from .env: " + baseUrl+"/swagger");
	}

}
