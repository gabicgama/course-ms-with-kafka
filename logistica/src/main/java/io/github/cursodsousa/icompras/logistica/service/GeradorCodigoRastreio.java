package io.github.cursodsousa.icompras.logistica.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class GeradorCodigoRastreio {

    private static final String CARACTERES =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final String SUFIXO = "BR";
    private static final int TAMANHO_CODIGO = 9;

    private final SecureRandom random = new SecureRandom();

    public String gerar() {
        var codigo = new StringBuilder(TAMANHO_CODIGO + SUFIXO.length());

        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            int indice = random.nextInt(CARACTERES.length());
            codigo.append(CARACTERES.charAt(indice));
        }

        return codigo.append(SUFIXO).toString();
    }
}