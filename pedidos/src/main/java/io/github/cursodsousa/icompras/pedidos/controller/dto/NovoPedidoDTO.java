package io.github.cursodsousa.icompras.pedidos.controller.dto;

import java.util.List;

public record NovoPedidoDTO(Long codigoCliente, DadosPagamentoDTO dadosPagamentoDTO, List<ItemPedidoDTO> itens) {
}
