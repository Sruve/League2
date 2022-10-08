package com.league.entidades;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PARTIDA")
public class Partida {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Integer id;

  @Column(name = "GAME_ID")
  private long gameId;

  @Column(name = "FECHA_CREACION")
  private long fechaCreacion;

  @Column(name = "DURACION")
  private int duracion;

  @Column(name = "FECHA_CREACION_T")
  private long fechaCreacionT;

  @Column(name = "FECHA_FINALIZACION_T")
  private Long fechaFinalizacionT;

  @Column(name = "TIPO_PARTIDA")
  private String tipoPartida;

  @Column(name = "PARCHE")
  private String parche;

  @OneToMany
  @JoinColumn(name = "ID_PARTIDA")
  private List<PartidaCuenta> partidaCuentas = new ArrayList<>();


  public Partida(LOLMatch lolMatch) {
    this.gameId = lolMatch.getGameId();
    this.fechaCreacion = lolMatch.getGameCreation();
    this.duracion = lolMatch.getGameDuration();
    this.fechaCreacionT = lolMatch.getGameStartTimestamp();
    this.fechaFinalizacionT = lolMatch.getGameEndTimestamp();
    this.tipoPartida = lolMatch.getQueue().getApiName();
    this.parche = lolMatch.getGameVersion();
  }
}
