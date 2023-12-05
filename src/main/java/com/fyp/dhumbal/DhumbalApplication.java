package com.fyp.dhumbal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DhumbalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DhumbalApplication.class, args);
    }

}
