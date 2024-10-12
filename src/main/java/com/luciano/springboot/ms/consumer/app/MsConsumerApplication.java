package com.luciano.springboot.ms.consumer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class MsConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConsumerApplication.class, args);
    }

}
