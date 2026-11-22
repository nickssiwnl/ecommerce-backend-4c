package br.edu.unifio.linkwork.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.edu.unifio.linkwork.enums.StatusProjeto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Projeto publicado por um contratante (EMPRESA ou PESSOA_FISICA) - ERS RF02.
 */
@Entity
@Getter
@Setter
public class Projeto {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank (message = "O título é obrigatório")
    @Size (min = 5, max = 120, message = "O título deve ter entre 5 e 120 caracteres")
    @Column (nullable = false, length = 120)
    private String titulo;

    // RN03: projeto sem descrição mínima não pode ser publicado
    @NotBlank (message = "A descrição é obrigatória")
    @Size (min = 30, max = 2000, message = "A descrição deve ter no mínimo 30 e no máximo 2000 caracteres")
    @Column (nullable = false, length = 2000)
    private String descricao;

    @NotNull (message = "O valor (orçamento) é obrigatório")
    @DecimalMin (value = "50.00", message = "O valor mínimo de um projeto é R$ 50,00")
    @Column (nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @NotNull (message = "O prazo é obrigatório")
    @FutureOrPresent (message = "O prazo não pode estar no passado")
    private LocalDate prazo;

    // Localização do projeto (cidade ou "Remoto")
    @Size (max = 100, message = "A cidade deve ter no máximo 100 caracteres")
    private String cidade;

    // Requisitos separados por vírgula. Ex.: "Java, Spring Boot, MySQL"
    @Size (max = 500, message = "Os requisitos devem ter no máximo 500 caracteres")
    private String requisitos;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 25)
    private StatusProjeto status;

    private LocalDateTime dataPublicacao;

    @NotNull (message = "A área do projeto é obrigatória")
    @ManyToOne
    private Area area;

    @NotNull (message = "O contratante do projeto é obrigatório")
    @ManyToOne
    private Usuario contratante;

    // Preenchido automaticamente quando uma proposta é aceita
    @ManyToOne
    private Usuario freelancer;
}
