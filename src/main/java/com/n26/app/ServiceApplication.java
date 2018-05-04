package com.n26.app;

import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*******************************************************
 * This is the main entry point or bootstrapping of this service.<br>
 * It is a spring boot self-hosted, self contained microservice used for tracking transactions<br>
 * and providing statistics via rest endpoints.
 * 
 * Upon application launch via java -jar app.jar,
 * 
 * @author pgobin
 *
 */
@SpringBootApplication
public class ServiceApplication {

	private static final Logger log = LogManager.getLogger(ServiceApplication.class);

	public static void main(String[] args)
	{
		log.info("Starting N26Test service...");
		SpringApplication.run(ServiceApplication.class, args);
		Instant instant = Instant.now();
		log.info(instant.toEpochMilli());
		log.info("*** Successfully started N26Test service at " + instant.toString() + " UTC");

	}
}
