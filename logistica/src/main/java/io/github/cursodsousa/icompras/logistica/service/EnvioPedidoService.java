package io.github.cursodsousa.icompras.logistica.service;

import io.github.cursodsousa.icompras.logistica.model.AtualizacaoEnvioPedido;
import io.github.cursodsousa.icompras.logistica.model.StatusPedido;
import io.github.cursodsousa.icompras.logistica.publisher.EnvioPedidoPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnvioPedidoService {

    private final EnvioPedidoPublisher publisher;
    private final GeradorCodigoRastreio geradorCodigoRastreio;

    public void enviar(Long codigoPedido, String urlNf) {

        var codigoRastreio = geradorCodigoRastreio.gerar();
        var atualizacaoRepresentation = new AtualizacaoEnvioPedido(codigoPedido, StatusPedido.ENVIADO, codigoRastreio);
        publisher.enviar(atualizacaoRepresentation);
    }

}
