package br.edu.unifio.linkwork.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.edu.unifio.linkwork.enums.FormaPagamento;
import br.edu.unifio.linkwork.enums.StatusPagamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Pagamento do contratante para o freelancer referente a um projeto (ERS - RF05).
 */
@Entity
@Getter
@Setter
public class Pagamento {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull (message = "O valor do pagamento é obrigatório")
    @DecimalMin (value = "0.01", message = "O valor do pagamento deve ser maior que zero")
    @Column (nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    private LocalDateTime data;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 15)
    private StatusPagamento status;

    @NotNull (message = "A forma de pagamento é obrigatória (PIX, CARTAO_CREDITO, CARTAO_DEBITO ou BOLETO)")
    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 20)
    private FormaPagamento forma;

    @NotNull (message = "O projeto do pagamento é obrigatório")
    @OneToOne
    @JoinColumn (unique = true)
    private Projeto projeto;
}
