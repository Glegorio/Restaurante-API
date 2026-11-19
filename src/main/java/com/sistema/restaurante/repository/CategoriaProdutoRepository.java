package com.sistema.restaurante.repository;

import com.sistema.restaurante.domain.entity.CategoriaProduto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Long> {
}
