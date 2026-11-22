package br.edu.unifio.linkwork.excecoes;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Corpo padrão das respostas de erro da API.
 */
@JsonInclude (JsonInclude.Include.NON_EMPTY)
public record ErroResposta(
        LocalDateTime dataHora,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<String> detalhes) {

    public static ErroResposta de(int status, String erro, String mensagem, String caminho) {
        return new ErroResposta(LocalDateTime.now(), status, erro, mensagem, caminho, List.of());
    }

    public static ErroResposta de(int status, String erro, String mensagem, String caminho, List<String> detalhes) {
        return new ErroResposta(LocalDateTime.now(), status, erro, mensagem, caminho, detalhes);
    }
}
