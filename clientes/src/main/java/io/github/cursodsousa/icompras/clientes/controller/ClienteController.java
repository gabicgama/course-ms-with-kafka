package io.github.cursodsousa.icompras.clientes.controller;

import io.github.cursodsousa.icompras.clientes.model.Cliente;
import io.github.cursodsousa.icompras.clientes.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> save(@RequestBody Cliente cliente) {
        Cliente clienteSaved = clienteService.save(cliente);
        URI location = URI.create("/clientes/" + clienteSaved.getCodigo());
        return ResponseEntity.created(location).body(clienteSaved);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Cliente> getCliente(@PathVariable("codigo") Long codigo) {
        return clienteService.findById(codigo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
