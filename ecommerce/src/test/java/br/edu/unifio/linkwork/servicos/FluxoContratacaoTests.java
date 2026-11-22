package br.edu.unifio.linkwork.servicos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.linkwork.entidades.Mensagem;
import br.edu.unifio.linkwork.entidades.Pagamento;
import br.edu.unifio.linkwork.entidades.Projeto;
import br.edu.unifio.linkwork.entidades.Proposta;
import br.edu.unifio.linkwork.entidades.Usuario;
import br.edu.unifio.linkwork.enums.FormaPagamento;
import br.edu.unifio.linkwork.enums.StatusProjeto;
import br.edu.unifio.linkwork.enums.StatusProposta;
import br.edu.unifio.linkwork.excecoes.RegraNegocioExcecao;

/**
 * Testa a funcionalidade específica do PI: proposta -> aceite -> pagamento -> início -> conclusão.
 * Usa os dados do import.sql e desfaz as alterações ao final de cada teste (@Transactional).
 */
@SpringBootTest
@Transactional
public class FluxoContratacaoTests {
    @Autowired
    private PropostaServico propostaServico;

    @Autowired
    private PagamentoServico pagamentoServico;

    @Autowired
    private ProjetoServico projetoServico;

    @Autowired
    private MensagemServico mensagemServico;

    @Test
    public void deveExecutarOFluxoCompletoDeContratacao() {
        // proposta 1: Lucas -> projeto 1 (R$ 4800)
        propostaServico.aceitar(1);
        assertEquals(StatusProjeto.AGUARDANDO_PAGAMENTO, projetoServico.buscarPorId(1).getStatus());

        var pagamento = new Pagamento();
        pagamento.setValor(new BigDecimal("4800.00"));
        pagamento.setForma(FormaPagamento.PIX);
        Projeto projeto = new Projeto();
        projeto.setId(1);
        pagamento.setProjeto(projeto);
        Pagamento salvo = pagamentoServico.cadastrar(pagamento);

        // RN10: sem confirmação o projeto não inicia
        assertThrows(RegraNegocioExcecao.class, () -> projetoServico.iniciar(1));

        pagamentoServico.confirmar(salvo.getId());
        assertEquals(StatusProjeto.EM_ANDAMENTO, projetoServico.iniciar(1).getStatus());

        // RN12
        assertEquals(StatusProjeto.FINALIZADO, projetoServico.concluir(1).getStatus());
    }

    @Test
    public void deveImpedirPropostaDuplicada() {
        var proposta = propostaServico.buscarPorId(1);
        var nova = new Proposta();
        nova.setValor(new BigDecimal("4000.00"));
        nova.setPrazoDias(20);
        nova.setMensagem("Nova proposta para o mesmo projeto");
        nova.setProjeto(proposta.getProjeto());
        nova.setFreelancer(proposta.getFreelancer());
        assertThrows(RegraNegocioExcecao.class, () -> propostaServico.cadastrar(nova));
        assertEquals(StatusProposta.PENDENTE, proposta.getStatus());
    }

    @Test
    public void deveImpedirMensagemDeUsuarioBloqueado() {
        var mensagem = new Mensagem();
        mensagem.setConteudo("Olá, tudo bem?");
        Usuario remetente = new Usuario();
        remetente.setId(5); // Rafael Costa - bloqueado no import.sql
        Usuario destinatario = new Usuario();
        destinatario.setId(1);
        mensagem.setRemetente(remetente);
        mensagem.setDestinatario(destinatario);
        assertThrows(RegraNegocioExcecao.class, () -> mensagemServico.enviar(mensagem));
    }

    @Test
    public void deveCalcularCompatibilidade() {
        // projeto 1 exige Java, Spring Boot, MySQL; Lucas (3) possui todos
        assertEquals(100, projetoServico.analisarCompatibilidade(1, 3).percentual());
        // Beatriz (4) não possui nenhum
        assertEquals(0, projetoServico.analisarCompatibilidade(1, 4).percentual());
    }
}
