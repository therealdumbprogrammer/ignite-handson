package com.thecodealchemist.main;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
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

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class IgniteDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(IgniteDemoApplication.class, args);
	}

	@Bean
	public ApplicationRunner applicationRunner(Ignite ignite) {
		return args -> {
//			 IgniteCache<String, String> cache = ignite.getOrCreateCache("dummy");
//			 //cache.put("key1", "value1");
//
//			System.out.println(cache.get("key1"));

			try(IgniteDataStreamer<String, String> ids = ignite.dataStreamer("dummy")) {
				ids.allowOverwrite(true);

			 	Files.
						lines(Paths.get("C:\\Users\\vimau\\Downloads\\ignite-demo\\ignite-demo\\src\\main\\resources\\result.csv"))
						.map(record -> record.split(","))
						.forEach(arr -> {
							ids.addData(arr[0], arr[3]);
						});
			}

			IgniteCache<String, String> cache = ignite.getOrCreateCache("dummy");
			System.out.println(cache.get("102"));


		};
	}

	@Bean
	public Ignite ignite() {
		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setDataStorageConfiguration(getDataStorageConfiguration());
		cfg.setCacheConfiguration(getCacheConfiguration());

		Ignite ignite = Ignition.start(cfg);
		ignite.cluster().state(ClusterState.ACTIVE);
		return ignite;
	}

	private CacheConfiguration getCacheConfiguration() {
		CacheConfiguration<String, String> cc = new CacheConfiguration<>();
		cc.setName("dummy");
		cc.setOnheapCacheEnabled(false);
		cc.setBackups(1);
		cc.setCacheMode(CacheMode.REPLICATED);
		cc.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

		return cc;
	}

	@NotNull
	private static DataStorageConfiguration getDataStorageConfiguration() {
		DataRegionConfiguration drc = new DataRegionConfiguration();
		drc.setName("my-data-region");
		drc.setInitialSize(10 * 1024 * 1024);
		drc.setMaxSize(40 * 1024 * 1024);
		drc.setPageEvictionMode(DataPageEvictionMode.RANDOM_2_LRU);
		drc.setPersistenceEnabled(true);

		DataStorageConfiguration dsc = new DataStorageConfiguration();
		dsc.setDefaultDataRegionConfiguration(drc);
		return dsc;
	}

}
