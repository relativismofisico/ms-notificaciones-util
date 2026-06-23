package co.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MsNotificacionesUtilApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsNotificacionesUtilApplication.class, args);
	}

}
