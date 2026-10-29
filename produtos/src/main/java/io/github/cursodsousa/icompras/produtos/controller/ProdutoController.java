package io.github.cursodsousa.icompras.produtos.controller;

import io.github.cursodsousa.icompras.produtos.model.Produto;
import io.github.cursodsousa.icompras.produtos.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deletar(@PathVariable Long codigo) {

        var produto = produtoService.findById(codigo).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Produto inexistente"
        ));
        produtoService.deletar(produto);

        return ResponseEntity.noContent().build();
    }
}
