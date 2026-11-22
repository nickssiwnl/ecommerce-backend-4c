package br.edu.unifio.linkwork.servicos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.linkwork.dtos.TotalRecebidoResposta;
import br.edu.unifio.linkwork.entidades.Pagamento;
import br.edu.unifio.linkwork.entidades.Projeto;
import br.edu.unifio.linkwork.entidades.Proposta;
import br.edu.unifio.linkwork.entidades.Usuario;
import br.edu.unifio.linkwork.enums.StatusPagamento;
import br.edu.unifio.linkwork.enums.StatusProjeto;
import br.edu.unifio.linkwork.enums.StatusProposta;
import br.edu.unifio.linkwork.enums.TipoUsuario;
import br.edu.unifio.linkwork.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.linkwork.excecoes.RegraNegocioExcecao;
import br.edu.unifio.linkwork.excecoes.RequisicaoInvalidaExcecao;
import br.edu.unifio.linkwork.repositorios.PagamentoRepositorio;
import br.edu.unifio.linkwork.repositorios.PropostaRepositorio;

@Service
public class PagamentoServico {
    @Autowired
    private PagamentoRepositorio pagamentoRepositorio;

    @Autowired
    private PropostaRepositorio propostaRepositorio;

    @Autowired
    private ProjetoServico projetoServico;

    @Autowired
    private UsuarioServico usuarioServico;

    public List<Pagamento> listar() {
        return pagamentoRepositorio.findAll(Sort.by(Sort.Direction.DESC, "data"));
    }

    public Pagamento buscarPorId(Integer id) {
        if (id == null) {
            throw new RequisicaoInvalidaExcecao("Informe o id do(a) pagamento");
        }
        return pagamentoRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Pagamento", id));
    }

    public List<Pagamento> listarPorStatus(StatusPagamento status) {
        return pagamentoRepositorio.findByStatusOrderByDataDesc(status);
    }

    public TotalRecebidoResposta totalRecebido(Integer freelancerId) {
        Usuario freelancer = usuarioServico.buscarPorId(freelancerId);
        if (freelancer.getTipo() != TipoUsuario.FREELANCER) {
            throw new RegraNegocioExcecao("O total recebido é calculado apenas para usuários do tipo FREELANCER");
        }
        BigDecimal total = pagamentoRepositorio.totalRecebidoPorFreelancer(freelancerId);
        return new TotalRecebidoResposta(freelancer.getId(), freelancer.getNome(), total);
    }

    @Transactional
    public Pagamento cadastrar(Pagamento pagamento) {
        pagamento.setId(null);
        Projeto projeto = projetoServico.buscarPorId(pagamento.getProjeto().getId());

        if (projeto.getStatus() != StatusProjeto.AGUARDANDO_PAGAMENTO) {
            throw new RegraNegocioExcecao("Só é possível registrar pagamento para projetos com status AGUARDANDO_PAGAMENTO. Status atual: "
                    + projeto.getStatus());
        }
        if (pagamentoRepositorio.existsByProjetoId(projeto.getId())) {
            throw new RegraNegocioExcecao("Já existe um pagamento registrado para este projeto");
        }
        validarValor(projeto, pagamento.getValor());

        pagamento.setProjeto(projeto);
        pagamento.setStatus(StatusPagamento.PENDENTE);
        pagamento.setData(LocalDateTime.now());
        return pagamentoRepositorio.save(pagamento);
    }

    @Transactional
    public Pagamento atualizar(Integer id, Pagamento dados) {
        Pagamento pagamento = buscarPorId(id);
        if (pagamento.getStatus() != StatusPagamento.PENDENTE) {
            throw new RegraNegocioExcecao("Somente pagamentos PENDENTES podem ser alterados");
        }
        validarValor(pagamento.getProjeto(), dados.getValor());
        pagamento.setValor(dados.getValor());
        pagamento.setForma(dados.getForma());
        // o projeto vinculado não pode ser alterado
        return pagamentoRepositorio.save(pagamento);
    }

    @Transactional
    public Pagamento confirmar(Integer id) {
        Pagamento pagamento = buscarPorId(id);
        if (pagamento.getStatus() != StatusPagamento.PENDENTE) {
            throw new RegraNegocioExcecao("O pagamento já está " + pagamento.getStatus());
        }
        pagamento.setStatus(StatusPagamento.CONFIRMADO);
        pagamento.setData(LocalDateTime.now());
        return pagamentoRepositorio.save(pagamento);
    }

    // RN11: transações confirmadas devem permanecer registradas
    @Transactional
    public void excluir(Integer id) {
        Pagamento pagamento = buscarPorId(id);
        if (pagamento.getStatus() == StatusPagamento.CONFIRMADO) {
            throw new RegraNegocioExcecao("Pagamentos CONFIRMADOS não podem ser excluídos: todas as transações devem permanecer registradas");
        }
        pagamentoRepositorio.delete(pagamento);
    }

    // Regra de negócio: o valor pago deve ser igual ao valor da proposta aceita
    private void validarValor(Projeto projeto, BigDecimal valor) {
        Proposta aceita = propostaRepositorio.findFirstByProjetoIdAndStatus(projeto.getId(), StatusProposta.ACEITA)
                .orElseThrow(() -> new RegraNegocioExcecao("O projeto não possui proposta aceita"));
        if (aceita.getValor().compareTo(valor) != 0) {
            throw new RegraNegocioExcecao("O valor do pagamento (R$ " + valor
                    + ") deve ser igual ao valor da proposta aceita (R$ " + aceita.getValor() + ")");
        }
    }
}
