package br.edu.unifio.linkwork.excecoes;

/**
 * Lançada quando uma operação viola uma regra de negócio ou
 * conflita com o estado atual dos dados. Gera resposta 409 Conflict.
 */
public class RegraNegocioExcecao extends RuntimeException {

    public RegraNegocioExcecao(String mensagem) {
        super(mensagem);
    }
}
