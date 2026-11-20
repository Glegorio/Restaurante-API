package com.sistema.restaurante.service;

import com.sistema.restaurante.domain.entity.PedidoItem;
import com.sistema.restaurante.domain.enums.StatusItemPedido;
import com.sistema.restaurante.dto.CozinhaItemResponse;
import com.sistema.restaurante.exception.RegraNegocioException;
import com.sistema.restaurante.repository.PedidoItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CozinhaService {

    private final PedidoItemRepository pedidoItemRepository;
    public CozinhaService(PedidoItemRepository pedidoItemRepository) {
        this.pedidoItemRepository = pedidoItemRepository;
    }

    public List<CozinhaItemResponse> listarItensPendentes(){
        return pedidoItemRepository.findByStatusOrderByIdAsc(StatusItemPedido.PENDENTE)
                .stream()
                .map(CozinhaItemResponse::fromEntity)
                .toList();
    }

    public List<CozinhaItemResponse> listarItensEmPreparo(){
        return pedidoItemRepository.findByStatusOrderByIdAsc(StatusItemPedido.EM_PREPARO)
                .stream()
                .map(CozinhaItemResponse::fromEntity)
                .toList();
    }

    public CozinhaItemResponse iniciarPreparo(Long itemId){
        PedidoItem item = buscarItemPorId(itemId);
        if(item.getStatus() != StatusItemPedido.PENDENTE){
            throw new RegraNegocioException("Somente itens pendentes podem iniciar preparo");
        }
        item.setStatus(StatusItemPedido.EM_PREPARO);
        return  CozinhaItemResponse.fromEntity(pedidoItemRepository.save(item));
    }

    public CozinhaItemResponse marcarComoPronto(Long itemId){
        PedidoItem item = buscarItemPorId(itemId);
        if(item.getStatus() != StatusItemPedido.EM_PREPARO){
            throw new RegraNegocioException("Somente itens em preparo podem ser marcados como prontos");
        }
        item.setStatus(StatusItemPedido.PRONTO);
        return  CozinhaItemResponse.fromEntity(pedidoItemRepository.save(item));
    }

    public CozinhaItemResponse entregarItem(Long itemId){
        PedidoItem item = buscarItemPorId(itemId);
        if(item.getStatus() != StatusItemPedido.PRONTO){
            throw new RegraNegocioException("Somente itens em prontos podem ser entregues");
        }
        item.setStatus(StatusItemPedido.ENTREGUE);
        return  CozinhaItemResponse.fromEntity(pedidoItemRepository.save(item));
    }

    private PedidoItem buscarItemPorId(Long itemId){
        return pedidoItemRepository.findById(itemId)
                .orElseThrow(() -> new RegraNegocioException("Item do pedido não encontrado"));
    }
}
