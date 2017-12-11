package io.sproutmoney.sproutweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySources({
		@PropertySource("classpath:auth/auth0.properties")
})
public class SproutWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(SproutWebApplication.class, args);
	}
}
