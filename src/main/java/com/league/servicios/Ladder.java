package com.league.servicios;

import com.league.SecretFile;
import com.league.entidades.Cuenta;
import com.league.entidades.Partida;
import com.league.repositorios.CuentaRepository;
import com.league.repositorios.PartidaCuentaRepository;
import com.league.repositorios.PartidaRepository;
import lombok.extern.slf4j.Slf4j;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard;
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;
import no.stelar7.api.r4j.basic.constants.types.lol.MatchlistMatchType;
import no.stelar7.api.r4j.basic.constants.types.lol.TierDivisionType;
import no.stelar7.api.r4j.basic.utils.LazyList;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.impl.lol.builders.matchv5.match.MatchBuilder;
import no.stelar7.api.r4j.impl.lol.raw.LeagueAPI;
import no.stelar7.api.r4j.pojo.lol.league.LeagueEntry;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Component
@Slf4j
public class Ladder {

  final R4J r4J = new R4J(SecretFile.CREDS);
  @Autowired CuentaRepository cuentaRepository;
  @Autowired PartidaRepository partidaRepository;
  @Autowired PartidaCuentaRepository partidaCuentaRepository;
  @Autowired ProcesarPartida procesarPartida;

  MatchBuilder mb = new MatchBuilder(RegionShard.EUROPE);

  public void barrerLadder() {
    List<LeagueEntry> dataAcumulada = new ArrayList<>();

    log.info("Comienza barrido del Ladder");
    log.info("Barrido Master");
    boolean masDatos = true;
    int page = 1;
    while (masDatos) {
      List<LeagueEntry> data = llamadaLadder(TierDivisionType.MASTER_I, page++);
      if (data != null) dataAcumulada.addAll(data);
      else masDatos = false;
    }

    log.info("Barrido Grandmaster");
    masDatos = true;
    page = 1;
    while (masDatos) {
      List<LeagueEntry> data = llamadaLadder(TierDivisionType.GRANDMASTER_I, page++);
      if (data != null) dataAcumulada.addAll(data);
      else masDatos = false;
    }

    log.info("Barrido Challenger");
    masDatos = true;
    page = 1;
    while (masDatos) {
      List<LeagueEntry> data = llamadaLadder(TierDivisionType.CHALLENGER_I, page++);
      if (data != null) dataAcumulada.addAll(data);
      else masDatos = false;
    }

    log.info("Comprobar cuentas");
    for (LeagueEntry leagueEntry : dataAcumulada) {
      String summonerId = leagueEntry.getSummonerId();
      Cuenta cuenta = cuentaRepository.findBySummonerId(summonerId).orElse(null);
      if (cuenta == null) {
        Summoner summoner = llamadaSummonerBySummonerId(summonerId);
        cuenta = new Cuenta(summoner);
        cuenta.updateRank(leagueEntry);
        cuentaRepository.save(cuenta);
      }
    }
    log.info("Finaliza barrido del Ladder");
    barrerPartidas();
  }

  @EventListener(ApplicationReadyEvent.class)
  public void barrerPartidas() {
    List<Cuenta> cuentas = cuentaRepository.findAllByOrderByLpsDesc();
    int contador = 1;
    for (Cuenta cuenta : cuentas) {
      Summoner summoner = llamadaSummonerByPuuid(cuenta.getPuuid());
      log.info(summoner.getName() + " " + contador++);
      LazyList<String> matchHistory =
          summoner
              .getLeagueGames()
              .withType(MatchlistMatchType.RANKED)
              .withQueue(GameQueueType.TEAM_BUILDER_RANKED_SOLO)
              .withPlatform(LeagueShard.EUW1)
              .getLazy();
      for (String matchId : matchHistory) {
        if (!matchId.contains("EUW")) continue;
        Partida partida =
            partidaRepository.findByGameId(Long.parseLong(matchId.substring(5))).orElse(null);
        if (partida == null) {
          LOLMatch match = llamadaMatch(matchId);
          if (match == null || match.getGameId() == 0) continue;
          partida = new Partida(match);
          partidaRepository.save(partida);
          procesarPartida.procesarPartida(match, partida);
        }
      }
    }
  }

  public void procesarRank() {
    log.warn("Scheduled procesarRank: Comienza");
    List<Cuenta> cuentas = cuentaRepository.findAllByOrderByFechaRevisionDesc();
    for (Cuenta cuenta : cuentas) {
      List<LeagueEntry> leagueEntryList =
          LeagueAPI.getInstance().getLeagueEntries(LeagueShard.EUW1, cuenta.getSummonerId());
      for (LeagueEntry leagueEntry : leagueEntryList) {
        if (leagueEntry.getQueueType().equals(GameQueueType.RANKED_SOLO_5X5))
          cuenta.updateRank(leagueEntry);
      }
      cuentaRepository.save(cuenta);
    }
    log.warn("Scheduled procesarRank: Acaba");
  }

  private List<LeagueEntry> llamadaLadder(TierDivisionType tierDivisionType, int page) {
    List<LeagueEntry> data =
        LeagueAPI.getInstance()
            .getLeagueByTierDivision(
                LeagueShard.EUW1, GameQueueType.RANKED_SOLO_5X5, tierDivisionType, page);
    return data.isEmpty() ? null : data;
  }

  private Summoner llamadaSummonerBySummonerId(String summonerId) {
    return Summoner.bySummonerId(LeagueShard.EUW1, summonerId);
  }

  private Summoner llamadaSummonerByPuuid(String puuid) {
    return Summoner.byPUUID(LeagueShard.EUW1, puuid);
  }

  private LOLMatch llamadaMatch(String matchId) {
    return mb.withId(matchId).getMatch();
  }
}
