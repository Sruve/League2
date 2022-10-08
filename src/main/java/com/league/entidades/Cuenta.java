package com.league.entidades;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.stelar7.api.r4j.pojo.lol.league.LeagueEntry;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "CUENTA")
public class Cuenta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Integer id;

  @Column(name = "PUUID", unique = true)
  private String puuid;

  @Column(name = "ACCOUNT_ID")
  private String accountId;

  @Column(name = "SUMMONER_ID")
  private String summonerId;

  @Column(name = "NOMBRE")
  private String nombre;

  @Column(name = "NIVEL")
  private int nivel;

  @Column(name = "FECHA_REVISION")
  private Date fechaRevision;

  @Column(name = "RANGO")
  private String rank;

  @Column(name = "TIER")
  private String tier;

  @Column(name = "LPS")
  private int lps;

  @OneToMany
  @JoinColumn(name = "ID_CUENTA")
  private List<PartidaCuenta> partidaCuentas = new ArrayList<>();

  public Cuenta(Summoner summoner) {
    this.puuid = summoner.getPUUID();
    this.accountId = summoner.getAccountId();
    this.summonerId = summoner.getSummonerId();
    this.nombre = summoner.getName();
    this.nivel = summoner.getSummonerLevel();
    this.fechaRevision = new Date();
  }

  public void updateRank(LeagueEntry leagueEntry) {
    this.rank = leagueEntry.getRank();
    this.tier = leagueEntry.getTier();
    this.lps = leagueEntry.getLeaguePoints();
  }

  public void addPartidaCuenta(PartidaCuenta partidaCuenta) {
    this.partidaCuentas.add(partidaCuenta);
  }
}
