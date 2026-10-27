package io.github.cursodsousa.icompras.pedidos.publisher;

import io.github.cursodsousa.icompras.pedidos.model.Pedido;
import io.github.cursodsousa.icompras.pedidos.publisher.representation.DetalheItemPedidoRepresentation;
import io.github.cursodsousa.icompras.pedidos.publisher.representation.DetalhePedidoRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DetalhePedidoMapper {

    @Mapping(source = "clienteRepresentation.nome", target = "nome")
    @Mapping(source = "clienteRepresentation.cpf", target = "cpf")
    @Mapping(source = "clienteRepresentation.logradouro", target = "logradouro")
    @Mapping(source = "clienteRepresentation.numero", target = "numero")
    @Mapping(source = "clienteRepresentation.bairro", target = "bairro")
    @Mapping(source = "clienteRepresentation.email", target = "email")
    @Mapping(source = "clienteRepresentation.telefone", target = "telefone")
    @Mapping(source = "dataPedido", target = "dataPedido", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "itemPedidoList", target = "itens")
    DetalhePedidoRepresentation map(Pedido pedido);

}
