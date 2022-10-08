package com.league.repositorios;

import com.league.entidades.PartidaCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaCuentaRepository extends JpaRepository<PartidaCuenta, Integer> {

    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO RELA_PARTIDA_CUENTA (ID_PARTIDA,ID_CUENTA,EQUIPO) VALUES (?1,?2,?3)")
    int insertOneRow(int partida, int cuenta, int teamId);

}
