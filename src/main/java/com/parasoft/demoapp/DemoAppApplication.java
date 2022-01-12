package com.parasoft.demoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class DemoAppApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DemoAppApplication.class);

		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}
