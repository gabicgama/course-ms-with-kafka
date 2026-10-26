package io.github.cursodsousa.icompras.pedidos.validator;

import io.github.cursodsousa.icompras.pedidos.client.ClienteClient;
import io.github.cursodsousa.icompras.pedidos.client.ProdutosClient;
import io.github.cursodsousa.icompras.pedidos.model.ItemPedido;
import io.github.cursodsousa.icompras.pedidos.model.Pedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PedidoValidator {

    private final ProdutosClient produtosClient;
    private final ClienteClient clienteClient;

    public void validator(Pedido pedido) {
        Long codigoCliente = pedido.getCodigoCliente();
        validarCliente(codigoCliente);
        pedido.getItemPedidoList().forEach(this::validarItem);
    }

    private void validarCliente(Long codigoCliente) {

    }

    private void validarItem(ItemPedido item) {

    }
}
