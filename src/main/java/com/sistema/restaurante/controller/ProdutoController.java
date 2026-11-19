package com.sistema.restaurante.controller;

import com.sistema.restaurante.dto.ProdutoRequest;
import com.sistema.restaurante.dto.ProdutoResponse;
import com.sistema.restaurante.service.ProdutoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse cadastrar(@RequestBody ProdutoRequest produtoRequest){
        return produtoService.cadastrar(produtoRequest);
    }

    @GetMapping
    public Page<ProdutoResponse> listar(@PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC)
                                        Pageable pageable){
        return produtoService.listar(pageable);
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscarPorId(@PathVariable Long id){
        return produtoService.bucarPorId(id);
    }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(@PathVariable Long id, ProdutoRequest produtoRequest){
        return produtoService.atualizar(id, produtoRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id){
        produtoService.excluir(id);
    }
}
