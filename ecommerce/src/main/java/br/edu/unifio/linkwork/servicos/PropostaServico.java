package br.edu.unifio.linkwork.servicos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.linkwork.entidades.Projeto;
import br.edu.unifio.linkwork.entidades.Proposta;
import br.edu.unifio.linkwork.entidades.Usuario;
import br.edu.unifio.linkwork.enums.StatusProjeto;
import br.edu.unifio.linkwork.enums.StatusProposta;
import br.edu.unifio.linkwork.enums.TipoUsuario;
import br.edu.unifio.linkwork.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.linkwork.excecoes.RegraNegocioExcecao;
import br.edu.unifio.linkwork.excecoes.RequisicaoInvalidaExcecao;
import br.edu.unifio.linkwork.repositorios.ProjetoRepositorio;
import br.edu.unifio.linkwork.repositorios.PropostaRepositorio;

@Service
public class PropostaServico {
    @Autowired
    private PropostaRepositorio propostaRepositorio;

    @Autowired
    private ProjetoRepositorio projetoRepositorio;

    @Autowired
    private ProjetoServico projetoServico;

    @Autowired
    private UsuarioServico usuarioServico;

    public List<Proposta> listar() {
        return propostaRepositorio.findAll(Sort.by(Sort.Direction.DESC, "dataEnvio"));
    }

    public Proposta buscarPorId(Integer id) {
        if (id == null) {
            throw new RequisicaoInvalidaExcecao("Informe o id do(a) proposta");
        }
        return propostaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Proposta", id));
    }

    public List<Proposta> listarPorProjeto(Integer projetoId) {
        projetoServico.buscarPorId(projetoId);
        return propostaRepositorio.findByProjetoIdOrderByValorAsc(projetoId);
    }

    public List<Proposta> listarPorFreelancer(Integer freelancerId) {
        usuarioServico.buscarPorId(freelancerId);
        return propostaRepositorio.findByFreelancerIdOrderByDataEnvioDesc(freelancerId);
    }

    @Transactional
    public Proposta cadastrar(Proposta proposta) {
        proposta.setId(null);
        Projeto projeto = projetoServico.buscarPorId(proposta.getProjeto().getId());
        Usuario freelancer = usuarioServico.buscarPorId(proposta.getFreelancer().getId());

        // Regras de negócio para envio de proposta
        if (freelancer.getTipo() != TipoUsuario.FREELANCER) {
            throw new RegraNegocioExcecao("Somente usuários do tipo FREELANCER podem enviar propostas");
        }
        if (Boolean.TRUE.equals(freelancer.getBloqueado())) {
            throw new RegraNegocioExcecao("O freelancer '" + freelancer.getNome() + "' está bloqueado e não pode enviar propostas");
        }
        if (projeto.getStatus() != StatusProjeto.ABERTO) {
            throw new RegraNegocioExcecao("O projeto não está aberto para propostas. Status atual: " + projeto.getStatus());
        }
        if (propostaRepositorio.existsByProjetoIdAndFreelancerId(projeto.getId(), freelancer.getId())) {
            throw new RegraNegocioExcecao("O freelancer '" + freelancer.getNome() + "' já enviou uma proposta para este projeto");
        }

        proposta.setProjeto(projeto);
        proposta.setFreelancer(freelancer);
        proposta.setStatus(StatusProposta.PENDENTE);
        proposta.setDataEnvio(LocalDateTime.now());
        return propostaRepositorio.save(proposta);
    }

    @Transactional
    public Proposta atualizar(Integer id, Proposta dados) {
        Proposta proposta = buscarPorId(id);
        if (proposta.getStatus() != StatusProposta.PENDENTE) {
            throw new RegraNegocioExcecao("Somente propostas PENDENTES podem ser alteradas. Status atual: " + proposta.getStatus());
        }
        proposta.setValor(dados.getValor());
        proposta.setPrazoDias(dados.getPrazoDias());
        proposta.setMensagem(dados.getMensagem());
        // projeto e freelancer não podem ser trocados
        return propostaRepositorio.save(proposta);
    }

    @Transactional
    public void excluir(Integer id) {
        Proposta proposta = buscarPorId(id);
        if (proposta.getStatus() == StatusProposta.ACEITA) {
            throw new RegraNegocioExcecao("Uma proposta ACEITA não pode ser excluída, pois deu origem a uma contratação");
        }
        propostaRepositorio.delete(proposta);
    }

    /**
     * Funcionalidade específica do PI - contratação do freelancer:
     * aceita a proposta, recusa as demais propostas pendentes do projeto,
     * vincula o freelancer ao projeto e coloca o projeto em AGUARDANDO_PAGAMENTO.
     */
    @Transactional
    public Proposta aceitar(Integer id) {
        Proposta proposta = buscarPorId(id);
        Projeto projeto = proposta.getProjeto();

        if (proposta.getStatus() != StatusProposta.PENDENTE) {
            throw new RegraNegocioExcecao("Somente propostas PENDENTES podem ser aceitas. Status atual: " + proposta.getStatus());
        }
        if (projeto.getStatus() != StatusProjeto.ABERTO) {
            throw new RegraNegocioExcecao("O projeto não está mais aberto. Status atual: " + projeto.getStatus());
        }
        if (Boolean.TRUE.equals(proposta.getFreelancer().getBloqueado())) {
            throw new RegraNegocioExcecao("O freelancer está bloqueado e não pode ser contratado");
        }

        proposta.setStatus(StatusProposta.ACEITA);
        propostaRepositorio.save(proposta);

        for (Proposta outra : propostaRepositorio.findByProjetoIdAndStatus(projeto.getId(), StatusProposta.PENDENTE)) {
            outra.setStatus(StatusProposta.RECUSADA);
            propostaRepositorio.save(outra);
        }

        projeto.setFreelancer(proposta.getFreelancer());
        projeto.setStatus(StatusProjeto.AGUARDANDO_PAGAMENTO);
        projetoRepositorio.save(projeto);

        return proposta;
    }

    @Transactional
    public Proposta recusar(Integer id) {
        Proposta proposta = buscarPorId(id);
        if (proposta.getStatus() != StatusProposta.PENDENTE) {
            throw new RegraNegocioExcecao("Somente propostas PENDENTES podem ser recusadas. Status atual: " + proposta.getStatus());
        }
        proposta.setStatus(StatusProposta.RECUSADA);
        return propostaRepositorio.save(proposta);
    }
}
