package br.edu.unifio.linkwork.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.unifio.linkwork.entidades.Usuario;
import br.edu.unifio.linkwork.enums.TipoUsuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdNot(String documento, Integer id);

    // Consulta personalizada (método derivado): usuários por tipo
    List<Usuario> findByTipoOrderByNome(TipoUsuario tipo);

    // Consulta personalizada (método derivado): freelancers que possuem determinada competência
    List<Usuario> findByTipoAndCompetenciasContainingIgnoreCaseOrderByNome(TipoUsuario tipo, String competencia);
}
