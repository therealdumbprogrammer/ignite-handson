package com.thecodealchemist.main;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

@SpringBootApplication
public class IgniteDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(IgniteDemoApplication.class, args);
	}

	@Bean
	public ApplicationRunner applicationRunner(Ignite ignite) {
		return args -> {
			IgniteQueue<String> workQ = ignite.queue("workQ", 0, new CollectionConfiguration());

			if("Node1".equals(whichNode())) {
				Thread.sleep(30 * 1000);
				Stream.of("a", "b").forEach(elem -> workQ.put(elem));
			} else {
				while(true) {
					String item = workQ.take();
					System.out.println("item = " + item);
				}
			}


		};
	}

	public String whichNode() {
		return System.getProperty("whichNode");
	}

	@Bean
	public Ignite ignite() {
		Ignite ignite = Ignition.start();
		return ignite;
	}


}
