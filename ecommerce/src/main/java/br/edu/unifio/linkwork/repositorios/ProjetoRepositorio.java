package br.edu.unifio.linkwork.repositorios;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.unifio.linkwork.entidades.Projeto;
import br.edu.unifio.linkwork.enums.StatusProjeto;

public interface ProjetoRepositorio extends JpaRepository<Projeto, Integer> {

    boolean existsByAreaId(Integer areaId);

    boolean existsByContratanteIdOrFreelancerId(Integer contratanteId, Integer freelancerId);

    // Consulta personalizada (método derivado): projetos por status, ordenados pelo prazo
    List<Projeto> findByStatusOrderByPrazoAsc(StatusProjeto status);

    // Consulta personalizada (método derivado): painel de projetos ativos (RF06)
    List<Projeto> findByStatusInOrderByPrazoAsc(List<StatusProjeto> status);

    // Consulta personalizada (JPQL): busca e filtro de projetos abertos (RF03 / RN06)
    // Todos os filtros são opcionais: área, localização (cidade), faixa de valor e palavra-chave.
    @Query("""
            select p from Projeto p
            where p.status = br.edu.unifio.linkwork.enums.StatusProjeto.ABERTO
              and (:areaId is null or p.area.id = :areaId)
              and (:cidade is null or lower(p.cidade) like lower(concat('%', :cidade, '%')))
              and (:valorMin is null or p.valor >= :valorMin)
              and (:valorMax is null or p.valor <= :valorMax)
              and (:termo is null
                   or lower(p.titulo) like lower(concat('%', :termo, '%'))
                   or lower(p.descricao) like lower(concat('%', :termo, '%')))
            order by p.dataPublicacao desc
            """)
    List<Projeto> filtrar(@Param("areaId") Integer areaId,
                          @Param("cidade") String cidade,
                          @Param("valorMin") BigDecimal valorMin,
                          @Param("valorMax") BigDecimal valorMax,
                          @Param("termo") String termo);
}
