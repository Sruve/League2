package com.league.entidades;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;

import javax.persistence.*;
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

  @Column(name = "gameId")
  private long gameId;

  @Column(name = "fechaCreacion")
  private long fechaCreacion;

  @Column(name = "duracion")
  private int duracion;

  @Column(name = "fechaCreacionT")
  private long fechaCreacionT;

  @Column(name = "fechaFinalizacionT")
  private long fechaFinalizacionT;

  @ManyToMany
  @JoinTable(
      name = "RELA_PARTIDA_CUENTA",
      joinColumns = {@JoinColumn(name = "ID_PARTIDA")},
      inverseJoinColumns = {@JoinColumn(name = "ID_CUENTA")})
  private List<Cuenta> cuentas;

  public Partida(LOLMatch lolMatch) {
    this.gameId = lolMatch.getGameId();
    this.fechaCreacion = lolMatch.getGameCreation();
    this.duracion = lolMatch.getGameDuration();
    this.fechaCreacionT = lolMatch.getGameStartTimestamp();
    this.fechaFinalizacionT = lolMatch.getGameEndTimestamp();
  }
}
