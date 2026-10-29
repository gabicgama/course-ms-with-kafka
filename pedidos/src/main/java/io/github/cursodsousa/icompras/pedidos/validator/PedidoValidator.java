package io.github.cursodsousa.icompras.pedidos.validator;

import feign.FeignException;
import io.github.cursodsousa.icompras.pedidos.client.ClienteClient;
import io.github.cursodsousa.icompras.pedidos.client.ProdutosClient;
import io.github.cursodsousa.icompras.pedidos.client.representation.ClienteRepresentation;
import io.github.cursodsousa.icompras.pedidos.client.representation.ProdutoRepresentation;
import io.github.cursodsousa.icompras.pedidos.exceptions.ValidatorException;
import io.github.cursodsousa.icompras.pedidos.model.ItemPedido;
import io.github.cursodsousa.icompras.pedidos.model.Pedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoValidator {

    private final ProdutosClient produtosClient;
    private final ClienteClient clienteClient;

    public void validator(Pedido pedido) {
        Long codigoCliente = pedido.getCodigoCliente();
        validarCliente(codigoCliente);
        pedido.getItemPedidoList().forEach(this::validarItem);
    }

    private void validarCliente(Long codigoCliente) {

        try {
            ResponseEntity<ClienteRepresentation> response = clienteClient.getCliente(codigoCliente);
            ClienteRepresentation cliente = response.getBody();
            log.info("Cliente de código {} encontrado: {}", cliente.codigo(), cliente.nome());

            if(!cliente.ativo()){
                throw new ValidatorException("Cliente inativo!", "codigoCliente");
            }

        } catch (FeignException.NotFound e) {
            var message = String.format("Cliente de código %d não encontrado.", codigoCliente);
            throw new ValidatorException("codigoCliente", message);
        }

    }

    private void validarItem(ItemPedido item) {

        try {
            var response = produtosClient.getProduto(item.getCodigoProduto());
            ProdutoRepresentation produto = response.getBody();
            log.info("Produto de código {} encontrado: {}", produto.codigo(), produto.nome());

            if(!produto.ativo()){
                throw new ValidatorException("Produto inativo!", "produto");
            }

        } catch (FeignException.NotFound e) {
            var message = String.format("Produto de código %d não encontrado.", item.getCodigoProduto());
            throw new ValidatorException("codigoProduto", message);
        }
    }
}
