package br.edu.unifio.linkwork.dtos;

import java.util.List;

/**
 * Resultado da análise de compatibilidade entre as competências de um
 * freelancer e os requisitos de um projeto (ERS - RN07).
 */
public record CompatibilidadeResposta(
        Integer projetoId,
        String projeto,
        Integer freelancerId,
        String freelancer,
        int percentual,
        List<String> requisitosAtendidos,
        List<String> requisitosFaltantes) {
}
