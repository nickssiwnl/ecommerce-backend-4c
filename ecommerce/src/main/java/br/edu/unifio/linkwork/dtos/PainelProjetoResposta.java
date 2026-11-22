package br.edu.unifio.linkwork.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.edu.unifio.linkwork.enums.StatusPagamento;
import br.edu.unifio.linkwork.enums.StatusProjeto;

/**
 * Item do painel de projetos ativos (ERS - RF06 / RN13: status e prazos).
 */
public record PainelProjetoResposta(
        Integer projetoId,
        String titulo,
        StatusProjeto status,
        LocalDate prazo,
        long diasRestantes,
        boolean atrasado,
        BigDecimal valor,
        String contratante,
        String freelancer,
        StatusPagamento statusPagamento) {
}
