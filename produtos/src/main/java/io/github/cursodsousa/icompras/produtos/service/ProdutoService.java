package io.github.cursodsousa.icompras.produtos.service;

import io.github.cursodsousa.icompras.produtos.model.Produto;
import io.github.cursodsousa.icompras.produtos.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public Produto save(Produto produto) {

        return produtoRepository.save(produto);
    }

    public Optional<Produto> findById(Long codigo) {

        return produtoRepository.findById(codigo);
    }

    public void deletar(Produto produto) {
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }
}
