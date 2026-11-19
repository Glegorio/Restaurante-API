package com.sistema.restaurante.repository;

import com.sistema.restaurante.domain.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
}
