package br.edu.unifio.linkwork.dtos;

import java.math.BigDecimal;

public record TotalRecebidoResposta(
        Integer freelancerId,
        String freelancer,
        BigDecimal totalRecebido) {
}
