package com.moment.momentbackend;

import org.springframework.boot.SpringApplication;

public class TestMomentBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(MomentBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
