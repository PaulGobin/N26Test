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
 * Upon application launch via java -jar app.jar, all beans will be boot strapped into spring container.
 * 
 * Once started, assuming you are using the default port, you can access the swagger ui at <br/>
 * http://localhost:8088/swagger-ui.html
 * 
 * <b>The UI is protected via Spring Security, use user and password to login.</b>
 * 
 * 
 * @author pgobin
 *
 */
@SpringBootApplication
public class ServiceApplication {

	private static final Logger log = LogManager.getLogger(ServiceApplication.class);

	/***************************************************************8
	 * The main entry point that boot strapped this service 
	 * @param args
	 */
	public static void main(String[] args)
	{
		log.info("Starting N26Test service...");
		SpringApplication.run(ServiceApplication.class, args);
		Instant instant = Instant.now();
		log.info(instant.toEpochMilli());
		log.info("*** Successfully started N26Test service at " + instant.toString() + " UTC");
		
		
		long inthFuture = Instant.ofEpochMilli(Instant.now().plusMillis(0).toEpochMilli()).toEpochMilli();
		long now = Instant.now().toEpochMilli();
		if(inthFuture > now) {
			System.out.println("Time cannot be in the future");
		}
	}
}
