package br.edu.unifio.linkwork.repositorios;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.unifio.linkwork.entidades.Pagamento;
import br.edu.unifio.linkwork.enums.StatusPagamento;

public interface PagamentoRepositorio extends JpaRepository<Pagamento, Integer> {

    Optional<Pagamento> findByProjetoId(Integer projetoId);

    boolean existsByProjetoId(Integer projetoId);

    boolean existsByProjetoIdAndIdNot(Integer projetoId, Integer id);

    // Consulta personalizada (método derivado): pagamentos por status
    List<Pagamento> findByStatusOrderByDataDesc(StatusPagamento status);

    // Consulta personalizada (JPQL): total já recebido (pagamentos confirmados) por um freelancer
    @Query("""
            select coalesce(sum(p.valor), 0) from Pagamento p
            where p.status = br.edu.unifio.linkwork.enums.StatusPagamento.CONFIRMADO
              and p.projeto.freelancer.id = :freelancerId
            """)
    BigDecimal totalRecebidoPorFreelancer(@Param("freelancerId") Integer freelancerId);
}
