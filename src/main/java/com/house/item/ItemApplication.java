package com.house.item;

import com.house.item.dev.TestDataInit;
import com.house.item.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.persistence.EntityManager;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ItemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(EntityManager em, UserService userService) {
		return new TestDataInit(em, userService);
	}

}
