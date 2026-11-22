package br.edu.unifio.linkwork.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Área de atuação dos projetos (ex.: Desenvolvimento Web, Design).
 * Usada no filtro de projetos por área (ERS - RN06).
 */
@Entity
@Getter
@Setter
public class Area {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank (message = "O nome da área é obrigatório")
    @Size (max = 60, message = "O nome da área deve ter no máximo 60 caracteres")
    @Column (nullable = false, unique = true, length = 60)
    private String nome;

    @Size (max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String descricao;
}
