package com.league.entidades;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RELA_PARTIDA_CUENTA")
public class PartidaCuenta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY,
          cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "ID_CUENTA")
  private Cuenta cuenta;

  @ManyToOne(fetch = FetchType.LAZY,
          cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "ID_PARTIDA")
  private Partida partida;

  @Column(name = "equipo")
  private int equipo;

  public PartidaCuenta(Partida partida, Cuenta cuenta){
    this.partida = partida;
    this.cuenta = cuenta;
  }

}
