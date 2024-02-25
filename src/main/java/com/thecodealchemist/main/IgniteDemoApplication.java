package com.thecodealchemist.main;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
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

import java.util.concurrent.locks.Lock;

@SpringBootApplication
public class IgniteDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(IgniteDemoApplication.class, args);
	}

	@Bean
	public ApplicationRunner applicationRunner(Ignite ignite) {
		return args -> {
			 IgniteCache<String, String> cache = ignite.getOrCreateCache("locking-cache");

			System.out.println(whichNode() + " is coming up!");

			Lock lock = cache.lock("lock-key");

			try {
				System.out.println(whichNode() + " acquiring the lock");
				lock.lock();
				someTask();
				System.out.println(whichNode() + " going to release the lock");
			} finally {
				lock.unlock();
			}
		};
	}

	private void someTask() throws InterruptedException {
		System.out.println(whichNode() + " current running the task");
		if("Node1".equals(whichNode())) {
			Thread.sleep(50 * 1000);
		}
		System.out.println(whichNode() + " exiting from the method");
	}

	private String whichNode() {
		return System.getProperty("whichNode");
	}

	@Bean
	public Ignite ignite() {
		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setCacheConfiguration(getCacheConfiguration());

		Ignite ignite = Ignition.start(cfg);
		return ignite;
	}

	private CacheConfiguration getCacheConfiguration() {
		CacheConfiguration<String, String> cc = new CacheConfiguration<>();
		cc.setName("locking-cache");
		cc.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

		return cc;
	}
}
