package kr.solta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SoltaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoltaApplication.class, args);
    }

}
