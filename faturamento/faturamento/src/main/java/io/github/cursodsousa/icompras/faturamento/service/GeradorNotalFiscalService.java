package io.github.cursodsousa.icompras.faturamento.service;

import io.github.cursodsousa.icompras.faturamento.bucket.BucketFile;
import io.github.cursodsousa.icompras.faturamento.bucket.BucketService;
import io.github.cursodsousa.icompras.faturamento.model.Pedido;
import io.github.cursodsousa.icompras.faturamento.publisher.FaturamentoPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeradorNotalFiscalService {

    private final NotaFiscalService notaFiscalService;
    private final BucketService bucketService;
    private final FaturamentoPublisher faturamentoPublisher;

    public void gerar(Pedido pedido) {
        log.info("Gerada nota fiscal para o pedido {}", pedido.codigo());

        try {
            byte[] bytes = notaFiscalService.gerarNota(pedido);

            String nomeArquivo = String.format("notafiscal_pedido_%d.pdf", pedido.codigo());

            var file = new BucketFile(nomeArquivo, new ByteArrayInputStream(bytes), MediaType.APPLICATION_PDF, bytes.length);

            bucketService.upload(file);
            String url = bucketService.getUrl(nomeArquivo);
            faturamentoPublisher.publicar(pedido, url);

            log.info("Gerada notal fiscal, nome do arquivo: {}", nomeArquivo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
