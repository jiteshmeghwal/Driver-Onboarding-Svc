package com.example.driveronboardingservice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Configure the executor for async tasks
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Set the number of core threads
        executor.setMaxPoolSize(10); // Set the maximum number of threads
        executor.setQueueCapacity(25); // Set the queue capacity
        executor.initialize();
        return executor;
    }
}