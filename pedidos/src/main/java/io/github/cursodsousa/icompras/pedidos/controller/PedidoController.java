package io.github.cursodsousa.icompras.pedidos.controller;

import io.github.cursodsousa.icompras.pedidos.controller.dto.AdicaoNovoPagamentoDTO;
import io.github.cursodsousa.icompras.pedidos.controller.dto.NovoPedidoDTO;
import io.github.cursodsousa.icompras.pedidos.controller.mappers.PedidoMapper;
import io.github.cursodsousa.icompras.pedidos.exceptions.ItemNaoEncontradoException;
import io.github.cursodsousa.icompras.pedidos.exceptions.ValidatorException;
import io.github.cursodsousa.icompras.pedidos.model.ErroResposta;
import io.github.cursodsousa.icompras.pedidos.publisher.DetalhePedidoMapper;
import io.github.cursodsousa.icompras.pedidos.publisher.representation.DetalhePedidoRepresentation;
import io.github.cursodsousa.icompras.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;
    private final DetalhePedidoMapper detalhePedidoMapper;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody NovoPedidoDTO novoPedidoDTO) {
        try {
            var pedido = pedidoMapper.mapPedido(novoPedidoDTO);
            var novoPedido = pedidoService.create(pedido);
            return ResponseEntity.ok(novoPedido.getCodigo());
        } catch (ValidatorException e) {
            var erro = new ErroResposta("Erro validação", e.getField(), e.getMessage());
            return ResponseEntity.badRequest().body(erro);
        }
    }

    @PostMapping("/pagamentos")
    public ResponseEntity<Object> adicionarNovoPagamento(@RequestBody AdicaoNovoPagamentoDTO dto) {
        try {
            pedidoService.adicionarNovoPagamento(dto.codigoPedido(), dto.dados(), dto.tipoPagamento());
            return ResponseEntity.noContent().build();
        } catch (ItemNaoEncontradoException e) {
            var erro = new ErroResposta("Item não encontrado", "codigoPedido", e.getMessage());
            return ResponseEntity.badRequest().body(erro);
        }
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<DetalhePedidoRepresentation> obterDetalhesPedido(@PathVariable("codigo") Long codigo) {

        return pedidoService.carregarDadosCompletosPedido(codigo)
                .map(detalhePedidoMapper::map)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
