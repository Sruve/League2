package com.league.servicios;

import com.league.SecretFile;
import com.league.entidades.Cuenta;
import com.league.entidades.Partida;
import com.league.entidades.PartidaCuenta;
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
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Component
@Slf4j
public class Ladder {

  final R4J r4J = new R4J(SecretFile.CREDS);
  @Autowired CuentaRepository cuentaRepository;
  @Autowired PartidaRepository partidaRepository;
  @Autowired PartidaCuentaRepository partidaCuentaRepository;

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
        cuentaRepository.save(cuenta);
      }
    }
    log.info("Finaliza barrido del Ladder");
  }

  public void barrerPartidas() {
    List<Cuenta> cuentas = cuentaRepository.findAll();

    for (Cuenta cuenta : cuentas) {
      Summoner summoner = llamadaSummonerByPuuid(cuenta.getPuuid());
      LazyList<String> matchHistory =
          summoner.getLeagueGames().withType(MatchlistMatchType.RANKED).withQueue(GameQueueType.TEAM_BUILDER_RANKED_SOLO).getLazy();
      for (String matchId : matchHistory) {
        long matchIdLong = Long.parseLong(matchId.substring(5));
        Partida partida = partidaRepository.findByGameId(matchIdLong).orElse(null);
        if (partida == null) {
          LOLMatch match = llamadaMatch(matchId);
          partida = new Partida(match);
          partidaRepository.save(partida);
          procesarPartida(match, partida);
        }
      }
    }
  }

  private void procesarPartida(LOLMatch match, Partida partida) {
    for (MatchParticipant participant : match.getParticipants()) {
      Cuenta cuenta = cuentaRepository.findByPuuid(participant.getPuuid()).orElse(null);
      if (cuenta == null) {
        Summoner summoner = llamadaSummonerByPuuid(participant.getPuuid());
        cuenta = new Cuenta(summoner);
        cuentaRepository.save(cuenta);
      }
      PartidaCuenta partidaCuenta = new PartidaCuenta(partida, cuenta);
      partidaCuenta.setEquipo(participant.getTeam().getValue());
      partidaCuentaRepository.save(partidaCuenta);
    }
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

  private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap, boolean order) {
    List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

    // Sorting the list based on values
    list.sort(
        (o1, o2) ->
            order
                ? o1.getValue().compareTo(o2.getValue()) == 0
                    ? o1.getKey().compareTo(o2.getKey())
                    : o1.getValue().compareTo(o2.getValue())
                : o2.getValue().compareTo(o1.getValue()) == 0
                    ? o2.getKey().compareTo(o1.getKey())
                    : o2.getValue().compareTo(o1.getValue()));
    return list.stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
  }
}
