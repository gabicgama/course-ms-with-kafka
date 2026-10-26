package io.github.cursodsousa.icompras.pedidos.service;

import io.github.cursodsousa.icompras.pedidos.client.ServicoBancarioClient;
import io.github.cursodsousa.icompras.pedidos.model.Pedido;
import io.github.cursodsousa.icompras.pedidos.repository.ItemPedidoRepository;
import io.github.cursodsousa.icompras.pedidos.repository.PedidoRepository;
import io.github.cursodsousa.icompras.pedidos.validator.PedidoValidator;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.jsse.PEMFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    private final PedidoValidator pedidoValidator;
    private final ServicoBancarioClient servicoBancarioClient;

    @Transactional
    public Pedido create(Pedido pedido) {
        pedidoValidator.validator(pedido);
        realizarPersistencia(pedido);
        enviarSolicitacaoPagamento(pedido);
        return pedido;
    }

    private void realizarPersistencia(Pedido pedido) {
        pedidoRepository.save(pedido);
        itemPedidoRepository.saveAll(pedido.getItemPedidoList());
    }

    private void enviarSolicitacaoPagamento(Pedido pedido) {
        var chavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(chavePagamento);
    }
}
