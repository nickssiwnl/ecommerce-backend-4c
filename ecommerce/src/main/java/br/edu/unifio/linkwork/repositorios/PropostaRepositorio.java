package br.edu.unifio.linkwork.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.unifio.linkwork.entidades.Proposta;
import br.edu.unifio.linkwork.enums.StatusProposta;

public interface PropostaRepositorio extends JpaRepository<Proposta, Integer> {

    boolean existsByProjetoIdAndFreelancerId(Integer projetoId, Integer freelancerId);

    boolean existsByFreelancerId(Integer freelancerId);

    // Consulta personalizada (método derivado): propostas de um projeto, da mais barata para a mais cara
    List<Proposta> findByProjetoIdOrderByValorAsc(Integer projetoId);

    // Consulta personalizada (método derivado): propostas enviadas por um freelancer
    List<Proposta> findByFreelancerIdOrderByDataEnvioDesc(Integer freelancerId);

    List<Proposta> findByProjetoIdAndStatus(Integer projetoId, StatusProposta status);

    Optional<Proposta> findFirstByProjetoIdAndStatus(Integer projetoId, StatusProposta status);

    void deleteByProjetoId(Integer projetoId);
}
