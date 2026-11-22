package br.edu.unifio.linkwork.excecoes;

/**
 * Lançada quando os dados enviados são inválidos de acordo com uma
 * verificação feita na camada de serviço. Gera resposta 400 Bad Request.
 */
public class RequisicaoInvalidaExcecao extends RuntimeException {

    public RequisicaoInvalidaExcecao(String mensagem) {
        super(mensagem);
    }
}
