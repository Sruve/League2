package com.league.spring.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncLogger {

  @Async
  public void info(String message) {
    log.info(message);
  }

  @Async
  public void warn(String message) {
    log.warn(message);
  }

  @Async
  public void error(String message) {
    log.error(message);
  }
}
