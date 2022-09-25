package com.league.repositorios;

import com.league.entidades.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

    Optional<Cuenta> findBySummonerId(String summonerId);
    Optional<Cuenta> findByPuuid(String puuid);
}
