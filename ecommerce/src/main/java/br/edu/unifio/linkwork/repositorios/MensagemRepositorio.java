package br.edu.unifio.linkwork.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.unifio.linkwork.entidades.Mensagem;

public interface MensagemRepositorio extends JpaRepository<Mensagem, Integer> {

    boolean existsByRemetenteIdOrDestinatarioId(Integer remetenteId, Integer destinatarioId);

    // Consulta personalizada (JPQL): histórico da conversa entre dois usuários (RN09)
    @Query("""
            select m from Mensagem m
            where (m.remetente.id = :usuario1 and m.destinatario.id = :usuario2)
               or (m.remetente.id = :usuario2 and m.destinatario.id = :usuario1)
            order by m.dataEnvio asc
            """)
    List<Mensagem> buscarConversa(@Param("usuario1") Integer usuario1, @Param("usuario2") Integer usuario2);

    // Consulta personalizada (método derivado): mensagens de um projeto
    List<Mensagem> findByProjetoIdOrderByDataEnvioAsc(Integer projetoId);

    void deleteByProjetoId(Integer projetoId);
}
