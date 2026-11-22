package br.edu.unifio.linkwork.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Mensagem trocada entre usuários (ERS - RF04).
 * O conjunto de mensagens forma o histórico de conversas (RN09).
 */
@Entity
@Getter
@Setter
public class Mensagem {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank (message = "O conteúdo da mensagem é obrigatório")
    @Size (max = 1000, message = "A mensagem deve ter no máximo 1000 caracteres")
    @Column (nullable = false, length = 1000)
    private String conteudo;

    private LocalDateTime dataEnvio;

    @NotNull (message = "O remetente é obrigatório")
    @ManyToOne
    private Usuario remetente;

    @NotNull (message = "O destinatário é obrigatório")
    @ManyToOne
    private Usuario destinatario;

    // Opcional: projeto ao qual a conversa se refere
    @ManyToOne
    private Projeto projeto;
}
