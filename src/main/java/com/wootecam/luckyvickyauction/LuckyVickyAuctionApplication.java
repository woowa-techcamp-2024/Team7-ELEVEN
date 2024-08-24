package com.wootecam.luckyvickyauction;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class LuckyVickyAuctionApplication {

    public static void main(String[] args) {
        SpringApplication.run(LuckyVickyAuctionApplication.class, args);
    }


    @Bean
    public CommandLineRunner printEnvVariables(Environment env) {
        return args -> {
            System.out.println("DB_ENDPOINT: " + env.getProperty("DB_ENDPOINT"));
            System.out.println("DB_PORT: " + env.getProperty("DB_PORT"));
            System.out.println("DB_NAME: " + env.getProperty("DB_NAME"));
            System.out.println("DB_USERNAME: " + env.getProperty("DB_USERNAME"));
            System.out.println("REDIS_HOST: " + env.getProperty("REDIS_HOST"));
            System.out.println("REDIS_PORT: " + env.getProperty("REDIS_PORT"));
        };
    }
}
