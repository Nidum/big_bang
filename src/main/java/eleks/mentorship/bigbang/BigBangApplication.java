package eleks.mentorship.bigbang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class BigBangApplication {
	public static void main(String[] args) {
		SpringApplication.run(BigBangApplication.class, args);
	}
}