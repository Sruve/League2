package com.league;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

  public ThreadPoolTaskExecutor executor;

  @Bean(name = "procesarPartidasExecutor")
  public Executor asyncExecutor(){
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(1);
    executor.setQueueCapacity(Integer.MAX_VALUE);
    executor.setThreadNamePrefix("AsyncThread-");
    executor.initialize();
    this.executor = executor;
    return executor;
  }
}

