package com.league.repositorios;

import com.league.entidades.PartidaCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaCuentaRepository extends JpaRepository<PartidaCuenta, Integer> {}
