package team.mediasoft.wareshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@SpringBootApplication
public class WareshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(WareshopApplication.class, args);
	}

}
