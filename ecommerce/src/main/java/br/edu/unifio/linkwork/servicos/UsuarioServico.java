package br.edu.unifio.linkwork.servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.linkwork.entidades.Usuario;
import br.edu.unifio.linkwork.enums.TipoUsuario;
import br.edu.unifio.linkwork.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.linkwork.excecoes.RegraNegocioExcecao;
import br.edu.unifio.linkwork.excecoes.RequisicaoInvalidaExcecao;
import br.edu.unifio.linkwork.repositorios.MensagemRepositorio;
import br.edu.unifio.linkwork.repositorios.ProjetoRepositorio;
import br.edu.unifio.linkwork.repositorios.PropostaRepositorio;
import br.edu.unifio.linkwork.repositorios.UsuarioRepositorio;

@Service
public class UsuarioServico {
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ProjetoRepositorio projetoRepositorio;

    @Autowired
    private PropostaRepositorio propostaRepositorio;

    @Autowired
    private MensagemRepositorio mensagemRepositorio;

    public List<Usuario> listar() {
        return usuarioRepositorio.findAll(Sort.by("nome"));
    }

    public Usuario buscarPorId(Integer id) {
        if (id == null) {
            throw new RequisicaoInvalidaExcecao("Informe o id do(a) usuário");
        }
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Usuário", id));
    }

    public List<Usuario> listarPorTipo(TipoUsuario tipo) {
        return usuarioRepositorio.findByTipoOrderByNome(tipo);
    }

    public List<Usuario> buscarFreelancersPorCompetencia(String competencia) {
        List<Usuario> freelancers = usuarioRepositorio
                .findByTipoAndCompetenciasContainingIgnoreCaseOrderByNome(TipoUsuario.FREELANCER, competencia.trim());
        if (freelancers.isEmpty()) {
            throw new RecursoNaoEncontradoExcecao("Nenhum freelancer encontrado com a competência '" + competencia + "'");
        }
        return freelancers;
    }

    @Transactional
    public Usuario cadastrar(Usuario usuario) {
        usuario.setId(null);
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        validarDocumento(usuario);

        // RN02: e-mail já em uso
        if (usuarioRepositorio.existsByEmailIgnoreCase(usuario.getEmail())) {
            throw new RegraNegocioExcecao("O e-mail '" + usuario.getEmail() + "' já está em uso");
        }
        // RN01: identificação única
        if (usuarioRepositorio.existsByDocumento(usuario.getDocumento())) {
            throw new RegraNegocioExcecao("Já existe um usuário cadastrado com o documento informado");
        }
        usuario.setBloqueado(false);
        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Integer id, Usuario dados) {
        Usuario usuario = buscarPorId(id);
        dados.setEmail(dados.getEmail().trim().toLowerCase());
        validarDocumento(dados);

        if (usuarioRepositorio.existsByEmailIgnoreCaseAndIdNot(dados.getEmail(), id)) {
            throw new RegraNegocioExcecao("O e-mail '" + dados.getEmail() + "' já está em uso por outro usuário");
        }
        if (usuarioRepositorio.existsByDocumentoAndIdNot(dados.getDocumento(), id)) {
            throw new RegraNegocioExcecao("O documento informado já pertence a outro usuário");
        }
        if (dados.getTipo() != usuario.getTipo() && possuiVinculos(id)) {
            throw new RegraNegocioExcecao("Não é possível alterar o tipo de um usuário que já possui projetos, propostas ou mensagens");
        }

        usuario.setNome(dados.getNome());
        usuario.setEmail(dados.getEmail());
        usuario.setDocumento(dados.getDocumento());
        usuario.setTipo(dados.getTipo());
        usuario.setTelefone(dados.getTelefone());
        usuario.setCidade(dados.getCidade());
        usuario.setCompetencias(dados.getCompetencias());
        // o campo "bloqueado" só é alterado pelos endpoints de bloqueio (ADMIN)
        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public Usuario alterarBloqueio(Integer id, boolean bloquear) {
        Usuario usuario = buscarPorId(id);
        if (Boolean.TRUE.equals(usuario.getBloqueado()) == bloquear) {
            throw new RegraNegocioExcecao("O usuário já está " + (bloquear ? "bloqueado" : "desbloqueado"));
        }
        usuario.setBloqueado(bloquear);
        return usuarioRepositorio.save(usuario);
    }

    // Regra de negócio: usuário com histórico (projetos, propostas, mensagens) não pode ser excluído
    @Transactional
    public void excluir(Integer id) {
        Usuario usuario = buscarPorId(id);
        if (possuiVinculos(id)) {
            throw new RegraNegocioExcecao("O usuário '" + usuario.getNome()
                    + "' possui projetos, propostas ou mensagens registradas e não pode ser excluído. Utilize o bloqueio.");
        }
        usuarioRepositorio.delete(usuario);
    }

    // RN01: freelancer e pessoa física usam CPF (11 dígitos); empresa usa CNPJ (14 dígitos)
    private void validarDocumento(Usuario usuario) {
        int tamanhoEsperado = usuario.getTipo() == TipoUsuario.EMPRESA ? 14 : 11;
        if (usuario.getDocumento().length() != tamanhoEsperado) {
            throw new RequisicaoInvalidaExcecao(usuario.getTipo() == TipoUsuario.EMPRESA
                    ? "Usuários do tipo EMPRESA devem informar um CNPJ com 14 dígitos"
                    : "Usuários do tipo " + usuario.getTipo() + " devem informar um CPF com 11 dígitos");
        }
    }

    private boolean possuiVinculos(Integer id) {
        return projetoRepositorio.existsByContratanteIdOrFreelancerId(id, id)
                || propostaRepositorio.existsByFreelancerId(id)
                || mensagemRepositorio.existsByRemetenteIdOrDestinatarioId(id, id);
    }
}
