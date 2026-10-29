package io.github.cursodsousa.icompras.pedidos.service;

import io.github.cursodsousa.icompras.pedidos.model.enums.StatusPedido;
import io.github.cursodsousa.icompras.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtualizacaoStatusPedidoService {

    private final PedidoRepository pedidoRepository;

    @Transactional
    public void atualizarStatus(Long codigo, StatusPedido status, String urlNf, String rastreio) {

        pedidoRepository.findById(codigo).ifPresent(pedido -> {
            if (status != null) {
                pedido.setStatus(status);
            }
            if (urlNf != null) {
                pedido.setUrlNf(urlNf);
            }
            if (rastreio != null) {
                pedido.setCodigoRastreio(rastreio);
            }
        });
    }
}
