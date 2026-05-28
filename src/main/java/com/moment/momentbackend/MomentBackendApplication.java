package com.moment.momentbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MomentBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MomentBackendApplication.class, args);
    }

}
