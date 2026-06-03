package com.moment.momentbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
public class MomentBackendApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MomentBackendApplication.class, args);

        boolean batchProfileActive = Arrays.asList(context.getEnvironment().getActiveProfiles())
                .contains("batch");

        if (batchProfileActive) {
            int exitCode = SpringApplication.exit(context);
            System.exit(exitCode);
        }
    }
}
