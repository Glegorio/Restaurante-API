package com.sistema.restaurante.repository;

import com.sistema.restaurante.domain.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRespository extends JpaRepository<Produto, Long> {
}
