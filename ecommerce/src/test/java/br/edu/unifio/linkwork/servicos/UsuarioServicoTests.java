package br.edu.unifio.linkwork.servicos;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.linkwork.entidades.Usuario;
import br.edu.unifio.linkwork.enums.TipoUsuario;
import br.edu.unifio.linkwork.excecoes.RegraNegocioExcecao;
import br.edu.unifio.linkwork.excecoes.RequisicaoInvalidaExcecao;

@SpringBootTest
@Transactional
public class UsuarioServicoTests {
    @Autowired
    private UsuarioServico usuarioServico;

    private Usuario novoFreelancer(String email, String documento) {
        var usuario = new Usuario();
        usuario.setNome("Ana Teste");
        usuario.setEmail(email);
        usuario.setDocumento(documento);
        usuario.setTipo(TipoUsuario.FREELANCER);
        usuario.setCompetencias("Java, Spring Boot");
        return usuario;
    }

    @Test
    public void deveCadastrarUmUsuarioNovo() {
        Usuario salvo = usuarioServico.cadastrar(novoFreelancer("ana.teste@email.com", "99988877766"));
        assertNotNull(salvo.getId());
        assertFalse(salvo.getBloqueado());
    }

    @Test
    public void deveImpedirEmailDuplicado() {
        // lucas.andrade@email.com já existe no import.sql
        assertThrows(RegraNegocioExcecao.class,
                () -> usuarioServico.cadastrar(novoFreelancer("LUCAS.ANDRADE@email.com", "99988877766")));
    }

    @Test
    public void deveImpedirCpfParaEmpresa() {
        Usuario empresa = novoFreelancer("empresa@email.com", "99988877766");
        empresa.setTipo(TipoUsuario.EMPRESA);
        assertThrows(RequisicaoInvalidaExcecao.class, () -> usuarioServico.cadastrar(empresa));
    }

    @Test
    public void deveImpedirExclusaoDeUsuarioComVinculos() {
        assertThrows(RegraNegocioExcecao.class, () -> usuarioServico.excluir(3));
    }
}
