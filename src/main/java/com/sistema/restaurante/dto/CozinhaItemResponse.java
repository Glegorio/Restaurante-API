package com.sistema.restaurante.dto;

import com.sistema.restaurante.domain.entity.PedidoItem;
import com.sistema.restaurante.domain.enums.StatusItemPedido;

import java.math.BigDecimal;

public record CozinhaItemResponse(
        Long itemId,
        Long pedidoId,
        Integer numeroMesa,
        String produtoNome,
        Integer quantidade,
        String observacao,
        BigDecimal precoUnitario,
        StatusItemPedido status
) {

    public static CozinhaItemResponse fromEntity(PedidoItem item){
        return new CozinhaItemResponse(
                item.getId(),
                item.getPedido().getId(),
                item.getPedido().getMesa().getNumero(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getObservacao(),
                item.getPrecoUnitario(),
                item.getStatus()
        );
    }
}
