package com.thecodealchemist.main;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteServices;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IgniteDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(IgniteDemoApplication.class, args);
	}

	@Bean
	public ApplicationRunner applicationRunner(Ignite ignite) {
		return args -> {
			IgniteServices igniteServices = ignite.services();

			igniteServices.deployClusterSingleton("singletonSvc", new SingleService());
		};
	}

	public static String whichNode() {
		return System.getProperty("whichNode");
	}

	@Bean
	public Ignite ignite() {
		IgniteConfiguration cfg = new IgniteConfiguration();

		Ignite ignite = Ignition.start(cfg);
		return ignite;
	}


}

