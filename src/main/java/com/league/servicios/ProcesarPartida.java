package com.league.servicios;

import com.league.SecretFile;
import com.league.entidades.Cuenta;
import com.league.entidades.Partida;
import com.league.repositorios.CuentaRepository;
import com.league.repositorios.PartidaCuentaRepository;
import lombok.extern.slf4j.Slf4j;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ProcesarPartida {

  final R4J r4J = new R4J(SecretFile.CREDS);
  @Autowired CuentaRepository cuentaRepository;
  @Autowired PartidaCuentaRepository partidaCuentaRepository;

  @Transactional
  public void procesarPartida(LOLMatch match, Partida partida) {
    for (MatchParticipant participant : match.getParticipants()) {
      Cuenta cuenta = buscar(participant.getPuuid());
      int teamId = participant.getTeam().getValue();
      partidaCuentaRepository.insertOneRow(partida.getId(), cuenta.getId(), teamId);
    }
  }

  @Transactional
  private Cuenta buscar(String puuid) {
    Cuenta cuenta = cuentaRepository.findByPuuid(puuid).orElse(null);
    if (cuenta == null) {
      Summoner summoner = llamadaSummonerByPuuid(puuid);
      cuenta = new Cuenta(summoner);
      cuentaRepository.save(cuenta);
    }
    return cuenta;
  }

  private Summoner llamadaSummonerByPuuid(String puuid) {
    return Summoner.byPUUID(LeagueShard.EUW1, puuid);
  }
}
