package com.example.springfunko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableCaching
@SpringBootApplication
@EnableMongoRepositories
public class SpringFunkoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringFunkoApplication.class, args);
    }

}
