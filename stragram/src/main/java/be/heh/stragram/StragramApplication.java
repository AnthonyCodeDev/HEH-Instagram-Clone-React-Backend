package be.heh.stragram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for Stragram - a social media backend API.
 * Built with Spring Boot following Hexagonal Architecture (Ports and Adapters).
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class StragramApplication {

    public static void main(String[] args) {
        SpringApplication.run(StragramApplication.class, args);
    }

}
