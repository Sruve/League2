package com.league.controladores;

import com.league.AsyncConfig;
import com.league.servicios.Ladder;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("api/v0/controller")
@Api(tags = "Controller")
public class Controller {

  @Autowired Ladder ladder;
  @Autowired AsyncConfig asyncConfig;

  @PostMapping("/barrerLadder")
  public void barrerLadder() {
    ladder.barrerLadder();
  }

  @PostMapping("/barrerPartidas")
  public void barrerPartidas() {
    ladder.barrerPartidas();
  }

  @PostMapping("/hilos")
  public String hilos() {
   return asyncConfig.executor.getThreadPoolExecutor().toString();
  }
}
