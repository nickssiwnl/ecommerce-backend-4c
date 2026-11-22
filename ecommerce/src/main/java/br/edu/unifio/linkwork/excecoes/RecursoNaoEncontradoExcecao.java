package br.edu.unifio.linkwork.excecoes;

/**
 * Lançada quando um recurso solicitado não existe. Gera resposta 404 Not Found.
 */
public class RecursoNaoEncontradoExcecao extends RuntimeException {

    public RecursoNaoEncontradoExcecao(String mensagem) {
        super(mensagem);
    }

    public RecursoNaoEncontradoExcecao(String recurso, Object id) {
        super(recurso + " com id " + id + " não encontrado(a)");
    }
}
