package com.example.ioproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IoprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(IoprojectApplication.class, args);
    }

}
