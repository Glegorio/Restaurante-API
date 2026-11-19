package com.sistema.restaurante.service;

import com.sistema.restaurante.domain.entity.Mesa;
import com.sistema.restaurante.domain.entity.Pedido;
import com.sistema.restaurante.domain.entity.PedidoItem;
import com.sistema.restaurante.domain.entity.Produto;
import com.sistema.restaurante.domain.enums.StatusItemPedido;
import com.sistema.restaurante.domain.enums.StatusMesa;
import com.sistema.restaurante.domain.enums.StatusPedido;
import com.sistema.restaurante.dto.PedidoItemRequest;
import com.sistema.restaurante.dto.PedidoItemResponse;
import com.sistema.restaurante.dto.PedidoRequest;
import com.sistema.restaurante.dto.PedidoResponse;
import com.sistema.restaurante.exception.RegraNegocioException;
import com.sistema.restaurante.repository.MesaRepository;
import com.sistema.restaurante.repository.PedidoItemRepository;
import com.sistema.restaurante.repository.PedidoRepository;
import com.sistema.restaurante.repository.ProdutoRespository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final ProdutoRespository produtoRespository;
    private final PedidoItemRepository pedidoItemRepository;

    public PedidoService(PedidoRepository pedidoRepository, MesaRepository mesaRepository,
                         ProdutoRespository produtoRespository, PedidoItemRepository pedidoItemRepository) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.produtoRespository = produtoRespository;
        this.pedidoItemRepository = pedidoItemRepository;
    }

    public PedidoResponse abrirPedido(PedidoRequest pedidoRequest) {
        Mesa mesa = mesaRepository.findById(pedidoRequest.mesaId())
                .orElseThrow(() -> new RegraNegocioException("Mesa inexistente"));

        if (mesa.getStatus() != StatusMesa.LIVRE){
            throw new RegraNegocioException("Mesa não está livre");
        }

        Pedido pedido = new Pedido();
        pedido.setMesa(mesa);
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setObservacao(pedidoRequest.observacao());

        mesa.setStatus(StatusMesa.OCUPADA);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        mesaRepository.save(mesa);

        return PedidoResponse.fromEntity(pedidoSalvo);
    }

    public Page<PedidoResponse> listar(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(PedidoResponse::fromEntity);
    }

    public PedidoResponse buscarPorId(Long id){
        Pedido pedido = buscarPedidoPorId(id);
        return PedidoResponse.fromEntity(pedido);
    }

    public PedidoItemResponse adicionarItem(Long pedidoId, PedidoItemRequest request){
        Pedido pedido = buscarPedidoPorId(pedidoId);
        if (pedido.getStatus() != StatusPedido.ABERTO){
            throw new RegraNegocioException("Só é possível adicionar item em pedidos abertos");
        }

        Produto produto = produtoRespository.findById(request.produtoId())
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado"));

        if (!produto.getDisponivel()){
            throw new RegraNegocioException("Produto indisponivel no cardápio");
        }

        if (request.quantidade() == null || request.quantidade()<= 0){
            throw new RegraNegocioException("A quantidade deve ser maior que zero");
        }

        PedidoItem pedidoItem = new PedidoItem();
        pedidoItem.setPedido(pedido);
        pedidoItem.setProduto(produto);
        pedidoItem.setQuantidade(request.quantidade());
        pedidoItem.setPrecoUnitario(produto.getPreco());
        pedidoItem.setObservacao(request.observacao());
        pedidoItem.setStatus(StatusItemPedido.PENDENTE);

        PedidoItem pedidoItemSalvo = pedidoItemRepository.save(pedidoItem);
        return PedidoItemResponse.fromEntity(pedidoItemSalvo);
    }

    public List<PedidoItemResponse> listarItens(Long pedidoId){
        buscarPedidoPorId(pedidoId);

        return pedidoItemRepository.findByPedidoId(pedidoId).stream().map(PedidoItemResponse::fromEntity).collect(Collectors.toList());
    }

    private Pedido buscarPedidoPorId(Long pedidoId){
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado"));
    }
}
