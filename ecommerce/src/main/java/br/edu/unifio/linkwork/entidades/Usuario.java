package br.edu.unifio.linkwork.entidades;

import br.edu.unifio.linkwork.enums.TipoUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Usuário da plataforma: freelancer, empresa ou pessoa física (ERS - RF01).
 * Obs.: os usuários de LOGIN (Spring Security) ficam em memória,
 * conforme permitido no enunciado; esta entidade representa o domínio.
 */
@Entity
@Getter
@Setter
public class Usuario {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank (message = "O nome é obrigatório")
    @Size (min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    @Column (nullable = false, length = 100)
    private String nome;

    @NotBlank (message = "O e-mail é obrigatório")
    @Email (message = "Informe um e-mail válido")
    @Column (nullable = false, unique = true, length = 120)
    private String email;

    // Identificação única: CPF (11 dígitos) ou CNPJ (14 dígitos), somente números
    @NotBlank (message = "O documento (CPF/CNPJ) é obrigatório")
    @Pattern (regexp = "\\d{11}|\\d{14}", message = "O documento deve conter 11 dígitos (CPF) ou 14 dígitos (CNPJ), somente números")
    @Column (nullable = false, unique = true, length = 14)
    private String documento;

    @NotNull (message = "O tipo de usuário é obrigatório (FREELANCER, EMPRESA ou PESSOA_FISICA)")
    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 20)
    private TipoUsuario tipo;

    @Size (max = 20, message = "O telefone deve ter no máximo 20 caracteres")
    private String telefone;

    @Size (max = 100, message = "A cidade deve ter no máximo 100 caracteres")
    private String cidade;

    // Competências separadas por vírgula. Ex.: "Java, Spring Boot, MySQL"
    @Size (max = 500, message = "As competências devem ter no máximo 500 caracteres")
    private String competencias;

    private Boolean bloqueado = false;
}
