package team.mediasoft.wareshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import team.mediasoft.wareshop.util.RestProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(RestProperties.class)
public class WareshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(WareshopApplication.class, args);
    }

}
