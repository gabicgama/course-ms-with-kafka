package io.github.cursodsousa.icompras.produtos.controller;

import io.github.cursodsousa.icompras.produtos.model.Produto;
import io.github.cursodsousa.icompras.produtos.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<Produto> save(@RequestBody Produto produto) {
        Produto produtoSaved = produtoService.save(produto);
        URI location = URI.create("produtos/" + produtoSaved.getCodigo());
        return ResponseEntity.created(location).body(produtoSaved);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Produto> getProduto(@PathVariable("codigo") Long codigo) {
        return produtoService.findById(codigo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
