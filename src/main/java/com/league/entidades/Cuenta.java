package com.league.entidades;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

  @Column(name = "puuid")
  private String puuid;

  @Column(name = "accountId")
  private String accountId;

  @Column(name = "summonerId")
  private String summonerId;

  @Column(name = "nombre")
  private String nombre;

  @Column(name = "nivel")
  private int nivel;

  @Column(name = "fechaRevision")
  private Date fechaRevision;
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

  public void addPartidaCuenta(PartidaCuenta partidaCuenta){
    this.partidaCuentas.add(partidaCuenta);
  }
}
