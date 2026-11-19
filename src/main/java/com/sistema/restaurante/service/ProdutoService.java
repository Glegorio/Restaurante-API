package com.sistema.restaurante.service;

import com.sistema.restaurante.domain.entity.CategoriaProduto;
import com.sistema.restaurante.domain.entity.Produto;
import com.sistema.restaurante.dto.ProdutoRequest;
import com.sistema.restaurante.dto.ProdutoResponse;
import com.sistema.restaurante.exception.RegraNegocioException;
import com.sistema.restaurante.repository.CategoriaProdutoRepository;
import com.sistema.restaurante.repository.ProdutoRespository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ProdutoService {

    private final ProdutoRespository produtoRespository;
    private final CategoriaProdutoRepository categoriaProdutoRepository;

    public ProdutoService(ProdutoRespository produtoRespository, CategoriaProdutoRepository categoriaProdutoRepository) {
        this.produtoRespository = produtoRespository;
        this.categoriaProdutoRepository = categoriaProdutoRepository;
    }

    public ProdutoResponse cadastrar(ProdutoRequest request){
        CategoriaProduto categoriaProduto = buscarCategoriaPorId(request.categoriaId());
        Produto produto = request.toEntity(categoriaProduto);
        Produto produtoSalvo = produtoRespository.save(produto);
        return ProdutoResponse.fromEntity(produtoSalvo);

    }

    public Page<ProdutoResponse> listar(Pageable pageable){
        return produtoRespository.findAll(pageable).map(ProdutoResponse::fromEntity);
    }

    public ProdutoResponse bucarPorId(Long id){
        Produto produto = buscarProdutoPorId(id);
        return ProdutoResponse.fromEntity(produto);
    }

    public ProdutoResponse atualizar(Long id, ProdutoRequest request){
        Produto produto = buscarProdutoPorId(id);
        CategoriaProduto categoriaProduto = buscarCategoriaPorId(request.categoriaId());
        request.preencher(produto, categoriaProduto);
        Produto produtoAtualizado = produtoRespository.save(produto);
        return ProdutoResponse.fromEntity(produtoAtualizado);
    }

    public void excluir(Long id){
        Produto produto = buscarProdutoPorId(id);
        produtoRespository.delete(produto);
    }

    // Métodos
    private Produto buscarProdutoPorId(Long id){
        return produtoRespository.findById(id).orElseThrow(() -> new RegraNegocioException("Produto não encontrado"));
    }
    private CategoriaProduto buscarCategoriaPorId(Long id){
        return categoriaProdutoRepository.findById(id).orElseThrow(() -> new RegraNegocioException("Categoria não encontrada"));
    }
}
