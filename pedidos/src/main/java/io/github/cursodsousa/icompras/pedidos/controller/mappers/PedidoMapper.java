package io.github.cursodsousa.icompras.pedidos.controller.mappers;

import io.github.cursodsousa.icompras.pedidos.controller.dto.ItemPedidoDTO;
import io.github.cursodsousa.icompras.pedidos.controller.dto.NovoPedidoDTO;
import io.github.cursodsousa.icompras.pedidos.model.ItemPedido;
import io.github.cursodsousa.icompras.pedidos.model.Pedido;
import io.github.cursodsousa.icompras.pedidos.model.StatusPedido;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Mapper(componentModel = "spring", uses = ItemPedidoMapper.class)
public interface PedidoMapper {

    ItemPedidoMapper ITEM_PEDIDO_MAPPER = Mappers.getMapper(ItemPedidoMapper.class);

    @Mapping(source = "itens", target = "itemPedidoList", qualifiedByName = "mapItens")
    @Mapping(source = "dadosPagamentoDTO", target = "dadosPagamento")
    Pedido mapPedido(NovoPedidoDTO novoPedidoDTO);

    @Named("mapItens")
    default List<ItemPedido> mapItens(List<ItemPedidoDTO> itemPedidoDTOList) {
        return itemPedidoDTOList.stream().map(ITEM_PEDIDO_MAPPER::mapItemPedido).toList();
    }

    @AfterMapping
    default void afterMapping(@MappingTarget Pedido pedido) {
        pedido.setStatus(StatusPedido.REALIZADO);
        pedido.setDataPedido(LocalDateTime.now());

        var total = calcularTotal(pedido);
        pedido.setTotal(total);

        pedido.getItemPedidoList().forEach(itemPedido -> itemPedido.setPedido(pedido));
    }

    private static BigDecimal calcularTotal(Pedido pedido) {
        return pedido.getItemPedidoList().stream()
                .map(itemPedido -> itemPedido.getValorUnitario().multiply(BigDecimal.valueOf(itemPedido.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).abs();
    }
}
