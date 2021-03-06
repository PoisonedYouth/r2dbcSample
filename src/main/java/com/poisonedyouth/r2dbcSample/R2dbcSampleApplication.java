package com.poisonedyouth.r2dbcSample;

import io.r2dbc.client.R2dbc;
import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class R2dbcSampleApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(R2dbcSampleApplication.class, args);
	}

	@Override
	public void run(String... args) {

		//Connect to H2 database
		H2ConnectionConfiguration configuration = H2ConnectionConfiguration.builder()
				.url("jdbc:h2:")
				.file("./testdb")
				.username("user")
				.password("password")
				.build();

		R2dbc r2dbc = new R2dbc(new H2ConnectionFactory(configuration));

		//Create table and insert sample data
		r2dbc.inTransaction(handle -> {
			handle.execute("CREATE TABLE IF NOT EXISTS sampleTable (sampleColumn int)");
			return handle.execute("INSERT INTO sampleTable VALUES ($1)", 100);
		}).subscribe();

		//Read values
		r2dbc.inTransaction(handle -> handle.select("SELECT * FROM sampleTable").mapRow(r -> r.get("sampleColumn", Integer.class)))
						.subscribe(s -> System.out.println("RECEIVED: " + s));

	}
}
