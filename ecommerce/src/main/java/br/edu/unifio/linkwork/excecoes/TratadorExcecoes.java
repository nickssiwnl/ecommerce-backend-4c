package br.edu.unifio.linkwork.excecoes;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Tratamento centralizado de exceções da API.
 */
@RestControllerAdvice
public class TratadorExcecoes {

    // 404 - recurso não encontrado
    @ExceptionHandler (RecursoNaoEncontradoExcecao.class)
    public ResponseEntity<ErroResposta> recursoNaoEncontrado(RecursoNaoEncontradoExcecao e, HttpServletRequest requisicao) {
        return resposta(HttpStatus.NOT_FOUND, e.getMessage(), requisicao);
    }

    // 409 - violação de regra de negócio
    @ExceptionHandler (RegraNegocioExcecao.class)
    public ResponseEntity<ErroResposta> regraNegocio(RegraNegocioExcecao e, HttpServletRequest requisicao) {
        return resposta(HttpStatus.CONFLICT, e.getMessage(), requisicao);
    }

    // 400 - dados inválidos verificados no serviço
    @ExceptionHandler (RequisicaoInvalidaExcecao.class)
    public ResponseEntity<ErroResposta> requisicaoInvalida(RequisicaoInvalidaExcecao e, HttpServletRequest requisicao) {
        return resposta(HttpStatus.BAD_REQUEST, e.getMessage(), requisicao);
    }

    // 400 - falhas de Bean Validation (@Valid)
    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> validacao(MethodArgumentNotValidException e, HttpServletRequest requisicao) {
        List<String> detalhes = e.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .sorted()
                .toList();
        var corpo = ErroResposta.de(400, "Bad Request",
                "Dados inválidos. Verifique os campos informados.", requisicao.getRequestURI(), detalhes);
        return ResponseEntity.badRequest().body(corpo);
    }

    // 400 - JSON malformado ou valor inválido (ex.: enum inexistente, data em formato errado)
    @ExceptionHandler (HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> jsonInvalido(HttpMessageNotReadableException e, HttpServletRequest requisicao) {
        return resposta(HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido. Verifique o formato do JSON, datas (aaaa-mm-dd) e valores de enumeração.",
                requisicao);
    }

    // 400 - parâmetro de URL com tipo errado (ex.: /projeto/abc)
    @ExceptionHandler (MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> tipoParametroInvalido(MethodArgumentTypeMismatchException e, HttpServletRequest requisicao) {
        return resposta(HttpStatus.BAD_REQUEST,
                "Valor inválido para o parâmetro '" + e.getName() + "': " + e.getValue(), requisicao);
    }

    // 400 - parâmetro obrigatório ausente
    @ExceptionHandler (MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResposta> parametroAusente(MissingServletRequestParameterException e, HttpServletRequest requisicao) {
        return resposta(HttpStatus.BAD_REQUEST,
                "Parâmetro obrigatório ausente: " + e.getParameterName(), requisicao);
    }

    // 409 - violação de integridade no banco (ex.: chave única)
    @ExceptionHandler (DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> integridade(DataIntegrityViolationException e, HttpServletRequest requisicao) {
        return resposta(HttpStatus.CONFLICT,
                "Operação viola a integridade dos dados (registro duplicado ou vinculado a outros registros).",
                requisicao);
    }

    // 404 - rota inexistente
    @ExceptionHandler (NoResourceFoundException.class)
    public ResponseEntity<ErroResposta> rotaInexistente(NoResourceFoundException e, HttpServletRequest requisicao) {
        return resposta(HttpStatus.NOT_FOUND, "Endpoint não encontrado: " + requisicao.getRequestURI(), requisicao);
    }

    // 405 - método HTTP não suportado
    @ExceptionHandler (HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResposta> metodoNaoSuportado(HttpRequestMethodNotSupportedException e, HttpServletRequest requisicao) {
        return resposta(HttpStatus.METHOD_NOT_ALLOWED,
                "Método " + e.getMethod() + " não suportado para este endpoint", requisicao);
    }

    // 415 - tipo de conteúdo não suportado (ex.: corpo enviado sem Content-Type: application/json)
    @ExceptionHandler (HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErroResposta> tipoConteudoNaoSuportado(HttpMediaTypeNotSupportedException e, HttpServletRequest requisicao) {
        return resposta(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Tipo de conteúdo não suportado: " + e.getContentType() + ". Utilize application/json", requisicao);
    }

    // 500 - erro inesperado
    @ExceptionHandler (Exception.class)
    public ResponseEntity<ErroResposta> erroGeral(Exception e, HttpServletRequest requisicao) {
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno no servidor: " + e.getMessage(), requisicao);
    }

    private ResponseEntity<ErroResposta> resposta(HttpStatus status, String mensagem, HttpServletRequest requisicao) {
        var corpo = ErroResposta.de(status.value(), status.getReasonPhrase(), mensagem, requisicao.getRequestURI());
        return ResponseEntity.status(status).body(corpo);
    }
}
