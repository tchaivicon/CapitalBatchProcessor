package com.hyundai.capital.capitalbatchprocessor;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing //Enable Spring Batch
@SpringBootApplication
public class CapitalBatchProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CapitalBatchProcessorApplication.class, args);
    }

}
