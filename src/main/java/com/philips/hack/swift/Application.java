package com.philips.hack.swift;




import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/*@ComponentScan("org.spring.examples.crud")
 @EntityScan("org.spring.examples.crud.model")*/

@Configuration
@EnableAutoConfiguration
@ComponentScan("com.philips.hack.swift")
public class Application extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
