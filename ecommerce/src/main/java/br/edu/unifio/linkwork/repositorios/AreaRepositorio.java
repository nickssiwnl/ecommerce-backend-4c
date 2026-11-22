package br.edu.unifio.linkwork.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.unifio.linkwork.entidades.Area;

public interface AreaRepositorio extends JpaRepository<Area, Integer> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Integer id);
}
