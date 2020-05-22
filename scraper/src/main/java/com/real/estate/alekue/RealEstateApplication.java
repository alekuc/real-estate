package com.real.estate.alekue;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
@SpringBootApplication
public class RealEstateApplication {

	public static void main(String[] args) {
		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
		Logger.getLogger("org.apache.http").setLevel(Level.OFF);
		SpringApplication.run(RealEstateApplication.class, args);
	}

}
