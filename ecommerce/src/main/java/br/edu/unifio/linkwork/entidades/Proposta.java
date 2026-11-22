package br.edu.unifio.linkwork.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.edu.unifio.linkwork.enums.StatusProposta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Proposta enviada por um freelancer para um projeto.
 */
@Entity
@Getter
@Setter
public class Proposta {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull (message = "O valor da proposta é obrigatório")
    @DecimalMin (value = "1.00", message = "O valor da proposta deve ser maior que zero")
    @Column (nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @NotNull (message = "O prazo em dias é obrigatório")
    @Min (value = 1, message = "O prazo deve ser de pelo menos 1 dia")
    @Max (value = 365, message = "O prazo deve ser de no máximo 365 dias")
    private Integer prazoDias;

    @NotBlank (message = "A mensagem da proposta é obrigatória")
    @Size (min = 10, max = 1000, message = "A mensagem deve ter entre 10 e 1000 caracteres")
    @Column (nullable = false, length = 1000)
    private String mensagem;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 15)
    private StatusProposta status;

    private LocalDateTime dataEnvio;

    @NotNull (message = "O projeto da proposta é obrigatório")
    @ManyToOne
    private Projeto projeto;

    @NotNull (message = "O freelancer da proposta é obrigatório")
    @ManyToOne
    private Usuario freelancer;
}
