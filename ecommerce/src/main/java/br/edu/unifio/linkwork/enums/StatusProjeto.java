package br.edu.unifio.linkwork.enums;

/**
 * Ciclo de vida de um projeto:
 * ABERTO -> AGUARDANDO_PAGAMENTO -> EM_ANDAMENTO -> FINALIZADO
 * (ou CANCELADO antes de iniciar).
 */
public enum StatusProjeto {
    ABERTO,
    AGUARDANDO_PAGAMENTO,
    EM_ANDAMENTO,
    FINALIZADO,
    CANCELADO
}
