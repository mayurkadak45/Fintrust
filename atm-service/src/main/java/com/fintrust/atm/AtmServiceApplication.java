package com.fintrust.atm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {"com.fintrust.atm", "com.fintrust.common"})
public class AtmServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AtmServiceApplication.class, args);
    }
}
