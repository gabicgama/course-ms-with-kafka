package io.github.cursodsousa.icompras.pedidos.controller;

import io.github.cursodsousa.icompras.pedidos.controller.dto.NovoPedidoDTO;
import io.github.cursodsousa.icompras.pedidos.controller.mappers.PedidoMapper;
import io.github.cursodsousa.icompras.pedidos.exceptions.ValidatorException;
import io.github.cursodsousa.icompras.pedidos.model.ErroResposta;
import io.github.cursodsousa.icompras.pedidos.model.Pedido;
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

    @GetMapping("/{codigo}")
    public ResponseEntity<Optional<Pedido>> getPedido(@PathVariable("codigo") Long codigo){
        Optional<Pedido> pedido = pedidoService.findById(codigo);
        return ResponseEntity.ok().body(pedido);
    }
}
