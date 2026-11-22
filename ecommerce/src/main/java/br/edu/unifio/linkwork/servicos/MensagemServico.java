package br.edu.unifio.linkwork.servicos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.linkwork.entidades.Mensagem;
import br.edu.unifio.linkwork.entidades.Usuario;
import br.edu.unifio.linkwork.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.linkwork.excecoes.RegraNegocioExcecao;
import br.edu.unifio.linkwork.excecoes.RequisicaoInvalidaExcecao;
import br.edu.unifio.linkwork.repositorios.MensagemRepositorio;

@Service
public class MensagemServico {
    @Autowired
    private MensagemRepositorio mensagemRepositorio;

    @Autowired
    private UsuarioServico usuarioServico;

    @Autowired
    private ProjetoServico projetoServico;

    public List<Mensagem> listar() {
        return mensagemRepositorio.findAll(Sort.by("dataEnvio"));
    }

    public Mensagem buscarPorId(Integer id) {
        if (id == null) {
            throw new RequisicaoInvalidaExcecao("Informe o id do(a) mensagem");
        }
        return mensagemRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Mensagem", id));
    }

    // RN09: histórico de conversas entre dois usuários
    public List<Mensagem> buscarConversa(Integer usuario1, Integer usuario2) {
        usuarioServico.buscarPorId(usuario1);
        usuarioServico.buscarPorId(usuario2);
        List<Mensagem> conversa = mensagemRepositorio.buscarConversa(usuario1, usuario2);
        if (conversa.isEmpty()) {
            throw new RecursoNaoEncontradoExcecao("Não há mensagens trocadas entre os usuários " + usuario1 + " e " + usuario2);
        }
        return conversa;
    }

    public List<Mensagem> listarPorProjeto(Integer projetoId) {
        projetoServico.buscarPorId(projetoId);
        return mensagemRepositorio.findByProjetoIdOrderByDataEnvioAsc(projetoId);
    }

    @Transactional
    public Mensagem enviar(Mensagem mensagem) {
        mensagem.setId(null);
        Usuario remetente = usuarioServico.buscarPorId(mensagem.getRemetente().getId());
        Usuario destinatario = usuarioServico.buscarPorId(mensagem.getDestinatario().getId());

        if (remetente.getId().equals(destinatario.getId())) {
            throw new RequisicaoInvalidaExcecao("O remetente e o destinatário devem ser usuários diferentes");
        }
        // RN08: usuário bloqueado não pode enviar mensagens
        if (Boolean.TRUE.equals(remetente.getBloqueado())) {
            throw new RegraNegocioExcecao("O usuário '" + remetente.getNome() + "' está bloqueado e não pode enviar mensagens");
        }

        mensagem.setRemetente(remetente);
        mensagem.setDestinatario(destinatario);
        if (mensagem.getProjeto() != null && mensagem.getProjeto().getId() != null) {
            mensagem.setProjeto(projetoServico.buscarPorId(mensagem.getProjeto().getId()));
        } else {
            mensagem.setProjeto(null);
        }
        mensagem.setDataEnvio(LocalDateTime.now());
        return mensagemRepositorio.save(mensagem);
    }

    @Transactional
    public Mensagem atualizar(Integer id, Mensagem dados) {
        Mensagem mensagem = buscarPorId(id);
        if (Boolean.TRUE.equals(mensagem.getRemetente().getBloqueado())) {
            throw new RegraNegocioExcecao("O remetente está bloqueado e não pode editar mensagens");
        }
        // apenas o conteúdo pode ser editado; remetente, destinatário e data são mantidos
        mensagem.setConteudo(dados.getConteudo());
        return mensagemRepositorio.save(mensagem);
    }

    @Transactional
    public void excluir(Integer id) {
        mensagemRepositorio.delete(buscarPorId(id));
    }
}
