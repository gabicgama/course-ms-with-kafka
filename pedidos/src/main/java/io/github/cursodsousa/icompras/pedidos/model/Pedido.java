package io.github.cursodsousa.icompras.pedidos.model;

import io.github.cursodsousa.icompras.pedidos.controller.dto.DadosPagamentoDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(name = "codigo_cliente", nullable = false)
    private Long codigoCliente;

    @Column(name = "data_pedido", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataPedido;

    @Column(name = "chave_pagamento", columnDefinition = "text")
    private String chavePagamento;

    @Column(columnDefinition = "text")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusPedido status;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal total;

    @Column(name = "codigo_rastreio", length = 255)
    private String codigoRastreio;

    @Column(name = "url_nf", columnDefinition = "text")
    private String urlNf;

    @Transient
    private DadosPagamentoDTO dadosPagamentoDTO;

    @OneToMany(mappedBy = "codigoPedido")
    private List<ItemPedido> itemPedidoList;

}
