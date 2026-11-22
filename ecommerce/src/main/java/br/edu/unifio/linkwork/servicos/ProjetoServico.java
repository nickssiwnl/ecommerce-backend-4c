package br.edu.unifio.linkwork.servicos;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.linkwork.dtos.CompatibilidadeResposta;
import br.edu.unifio.linkwork.dtos.PainelProjetoResposta;
import br.edu.unifio.linkwork.entidades.Pagamento;
import br.edu.unifio.linkwork.entidades.Projeto;
import br.edu.unifio.linkwork.entidades.Usuario;
import br.edu.unifio.linkwork.enums.StatusPagamento;
import br.edu.unifio.linkwork.enums.StatusProjeto;
import br.edu.unifio.linkwork.enums.TipoUsuario;
import br.edu.unifio.linkwork.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.linkwork.excecoes.RegraNegocioExcecao;
import br.edu.unifio.linkwork.excecoes.RequisicaoInvalidaExcecao;
import br.edu.unifio.linkwork.repositorios.MensagemRepositorio;
import br.edu.unifio.linkwork.repositorios.PagamentoRepositorio;
import br.edu.unifio.linkwork.repositorios.ProjetoRepositorio;
import br.edu.unifio.linkwork.repositorios.PropostaRepositorio;

@Service
public class ProjetoServico {
    @Autowired
    private ProjetoRepositorio projetoRepositorio;

    @Autowired
    private PropostaRepositorio propostaRepositorio;

    @Autowired
    private MensagemRepositorio mensagemRepositorio;

    @Autowired
    private PagamentoRepositorio pagamentoRepositorio;

    @Autowired
    private AreaServico areaServico;

    @Autowired
    private UsuarioServico usuarioServico;

    // ---------------------------------------------------------------- CRUD

    public List<Projeto> listar() {
        return projetoRepositorio.findAll(Sort.by(Sort.Direction.DESC, "dataPublicacao"));
    }

    public Projeto buscarPorId(Integer id) {
        if (id == null) {
            throw new RequisicaoInvalidaExcecao("Informe o id do(a) projeto");
        }
        return projetoRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Projeto", id));
    }

    public List<Projeto> listarPorStatus(StatusProjeto status) {
        return projetoRepositorio.findByStatusOrderByPrazoAsc(status);
    }

    @Transactional
    public Projeto cadastrar(Projeto projeto) {
        projeto.setId(null);
        Usuario contratante = usuarioServico.buscarPorId(projeto.getContratante().getId());

        // Regra de negócio: somente EMPRESA ou PESSOA_FISICA publicam projetos
        if (contratante.getTipo() == TipoUsuario.FREELANCER) {
            throw new RegraNegocioExcecao("Usuários do tipo FREELANCER não podem publicar projetos, apenas enviar propostas");
        }
        if (Boolean.TRUE.equals(contratante.getBloqueado())) {
            throw new RegraNegocioExcecao("O usuário '" + contratante.getNome() + "' está bloqueado e não pode publicar projetos");
        }

        projeto.setContratante(contratante);
        projeto.setArea(areaServico.buscarPorId(projeto.getArea().getId()));
        projeto.setFreelancer(null);
        projeto.setStatus(StatusProjeto.ABERTO);
        projeto.setDataPublicacao(LocalDateTime.now());
        return projetoRepositorio.save(projeto);
    }

    // RN04: projetos publicados podem ser editados enquanto estiverem ABERTOS
    @Transactional
    public Projeto atualizar(Integer id, Projeto dados) {
        Projeto projeto = buscarPorId(id);
        if (projeto.getStatus() != StatusProjeto.ABERTO) {
            throw new RegraNegocioExcecao("Somente projetos com status ABERTO podem ser editados. Status atual: "
                    + projeto.getStatus());
        }
        projeto.setTitulo(dados.getTitulo());
        projeto.setDescricao(dados.getDescricao());
        projeto.setValor(dados.getValor());
        projeto.setPrazo(dados.getPrazo());
        projeto.setCidade(dados.getCidade());
        projeto.setRequisitos(dados.getRequisitos());
        projeto.setArea(areaServico.buscarPorId(dados.getArea().getId()));
        // o contratante, o status e o freelancer não são alterados pela edição
        return projetoRepositorio.save(projeto);
    }

    // RN04 + RN11: exclusão permitida para projetos ABERTOS ou CANCELADOS sem pagamento confirmado
    @Transactional
    public void excluir(Integer id) {
        Projeto projeto = buscarPorId(id);
        if (projeto.getStatus() != StatusProjeto.ABERTO && projeto.getStatus() != StatusProjeto.CANCELADO) {
            throw new RegraNegocioExcecao("Projetos com status " + projeto.getStatus()
                    + " não podem ser excluídos. Somente projetos ABERTOS ou CANCELADOS.");
        }
        pagamentoRepositorio.findByProjetoId(id).ifPresent(pagamento -> {
            if (pagamento.getStatus() == StatusPagamento.CONFIRMADO) {
                throw new RegraNegocioExcecao("O projeto possui pagamento confirmado e não pode ser excluído (registro de transação)");
            }
            pagamentoRepositorio.delete(pagamento);
        });
        mensagemRepositorio.deleteByProjetoId(id);
        propostaRepositorio.deleteByProjetoId(id);
        projetoRepositorio.delete(projeto);
    }

    // ---------------------------------------------------------------- Busca e filtros (RF03)

    // RN05 e RN06: filtros por área, localização e valor; mensagem informativa quando não há resultados
    public List<Projeto> filtrar(Integer areaId, String cidade, BigDecimal valorMin, BigDecimal valorMax, String termo) {
        if (valorMin != null && valorMax != null && valorMin.compareTo(valorMax) > 0) {
            throw new RequisicaoInvalidaExcecao("O valor mínimo não pode ser maior que o valor máximo");
        }
        List<Projeto> projetos = projetoRepositorio.filtrar(areaId, vazioParaNulo(cidade), valorMin, valorMax, vazioParaNulo(termo));
        if (projetos.isEmpty()) {
            throw new RecursoNaoEncontradoExcecao("Nenhum projeto aberto encontrado para os filtros informados");
        }
        return projetos;
    }

    // ---------------------------------------------------------------- Ciclo de vida do projeto

    // RN10: o projeto só pode ser iniciado após a confirmação do pagamento
    @Transactional
    public Projeto iniciar(Integer id) {
        Projeto projeto = buscarPorId(id);
        if (projeto.getStatus() != StatusProjeto.AGUARDANDO_PAGAMENTO) {
            throw new RegraNegocioExcecao("Somente projetos com status AGUARDANDO_PAGAMENTO podem ser iniciados. Status atual: "
                    + projeto.getStatus());
        }
        Pagamento pagamento = pagamentoRepositorio.findByProjetoId(id)
                .orElseThrow(() -> new RegraNegocioExcecao("O projeto não pode ser iniciado: nenhum pagamento foi registrado"));
        if (pagamento.getStatus() != StatusPagamento.CONFIRMADO) {
            throw new RegraNegocioExcecao("O projeto não pode ser iniciado: o pagamento ainda não foi confirmado");
        }
        projeto.setStatus(StatusProjeto.EM_ANDAMENTO);
        return projetoRepositorio.save(projeto);
    }

    // RN12: projeto concluído deve ser marcado como FINALIZADO
    @Transactional
    public Projeto concluir(Integer id) {
        Projeto projeto = buscarPorId(id);
        if (projeto.getStatus() != StatusProjeto.EM_ANDAMENTO) {
            throw new RegraNegocioExcecao("Somente projetos EM_ANDAMENTO podem ser concluídos. Status atual: "
                    + projeto.getStatus());
        }
        projeto.setStatus(StatusProjeto.FINALIZADO);
        return projetoRepositorio.save(projeto);
    }

    @Transactional
    public Projeto cancelar(Integer id) {
        Projeto projeto = buscarPorId(id);
        if (projeto.getStatus() != StatusProjeto.ABERTO && projeto.getStatus() != StatusProjeto.AGUARDANDO_PAGAMENTO) {
            throw new RegraNegocioExcecao("Projetos com status " + projeto.getStatus() + " não podem ser cancelados");
        }
        boolean pagamentoConfirmado = pagamentoRepositorio.findByProjetoId(id)
                .map(p -> p.getStatus() == StatusPagamento.CONFIRMADO)
                .orElse(false);
        if (pagamentoConfirmado) {
            throw new RegraNegocioExcecao("O projeto possui pagamento confirmado e não pode ser cancelado");
        }
        projeto.setStatus(StatusProjeto.CANCELADO);
        return projetoRepositorio.save(projeto);
    }

    // ---------------------------------------------------------------- Painel de projetos ativos (RF06)

    // RN13: visualizar status e prazos dos projetos ativos
    public List<PainelProjetoResposta> painel(Integer usuarioId) {
        if (usuarioId != null) {
            usuarioServico.buscarPorId(usuarioId);
        }
        List<Projeto> ativos = projetoRepositorio.findByStatusInOrderByPrazoAsc(
                List.of(StatusProjeto.AGUARDANDO_PAGAMENTO, StatusProjeto.EM_ANDAMENTO));
        LocalDate hoje = LocalDate.now();

        return ativos.stream()
                .filter(p -> usuarioId == null
                        || p.getContratante().getId().equals(usuarioId)
                        || (p.getFreelancer() != null && p.getFreelancer().getId().equals(usuarioId)))
                .map(p -> {
                    long dias = ChronoUnit.DAYS.between(hoje, p.getPrazo());
                    StatusPagamento statusPagamento = pagamentoRepositorio.findByProjetoId(p.getId())
                            .map(Pagamento::getStatus).orElse(null);
                    return new PainelProjetoResposta(p.getId(), p.getTitulo(), p.getStatus(), p.getPrazo(),
                            dias, dias < 0, p.getValor(), p.getContratante().getNome(),
                            p.getFreelancer() != null ? p.getFreelancer().getNome() : null, statusPagamento);
                })
                .toList();
    }

    // ---------------------------------------------------------------- Compatibilidade (RN07)

    public CompatibilidadeResposta analisarCompatibilidade(Integer projetoId, Integer freelancerId) {
        Projeto projeto = buscarPorId(projetoId);
        Usuario freelancer = usuarioServico.buscarPorId(freelancerId);
        if (freelancer.getTipo() != TipoUsuario.FREELANCER) {
            throw new RegraNegocioExcecao("A análise de compatibilidade é feita apenas para usuários do tipo FREELANCER");
        }
        return calcularCompatibilidade(projeto, freelancer);
    }

    // Lista os projetos abertos mais compatíveis com as competências do freelancer
    public List<CompatibilidadeResposta> recomendarProjetos(Integer freelancerId) {
        Usuario freelancer = usuarioServico.buscarPorId(freelancerId);
        if (freelancer.getTipo() != TipoUsuario.FREELANCER) {
            throw new RegraNegocioExcecao("Recomendações estão disponíveis apenas para usuários do tipo FREELANCER");
        }
        List<CompatibilidadeResposta> recomendados = projetoRepositorio.findByStatusOrderByPrazoAsc(StatusProjeto.ABERTO)
                .stream()
                .filter(p -> !p.getContratante().getId().equals(freelancerId))
                .map(p -> calcularCompatibilidade(p, freelancer))
                .filter(c -> c.percentual() > 0)
                .sorted(Comparator.comparingInt(CompatibilidadeResposta::percentual).reversed())
                .toList();
        if (recomendados.isEmpty()) {
            throw new RecursoNaoEncontradoExcecao("Nenhum projeto aberto compatível com as competências do freelancer");
        }
        return recomendados;
    }

    private CompatibilidadeResposta calcularCompatibilidade(Projeto projeto, Usuario freelancer) {
        List<String> requisitos = separar(projeto.getRequisitos());
        Set<String> competencias = separar(freelancer.getCompetencias()).stream()
                .map(ProjetoServico::normalizar)
                .collect(Collectors.toSet());

        List<String> atendidos = new ArrayList<>();
        List<String> faltantes = new ArrayList<>();
        for (String requisito : requisitos) {
            if (competencias.contains(normalizar(requisito))) {
                atendidos.add(requisito);
            } else {
                faltantes.add(requisito);
            }
        }
        int percentual = requisitos.isEmpty() ? 100 : Math.round(atendidos.size() * 100f / requisitos.size());
        return new CompatibilidadeResposta(projeto.getId(), projeto.getTitulo(), freelancer.getId(),
                freelancer.getNome(), percentual, atendidos, faltantes);
    }

    private static List<String> separar(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return Arrays.stream(texto.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }

    // remove acentos, espaços extras e diferença entre maiúsculas/minúsculas
    private static String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    private static String vazioParaNulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
