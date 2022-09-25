package com.league.entidades.catalogos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TIPO_PARTIDA")
public class TipoPartida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CODIGO")
    private String codigo;

    @Column(name = "valor")
    private String valor;


}
