package br.edu.unifio.linkwork.configuracoes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Autenticação HTTP Basic com usuários em memória.
 *
 * Perfis:
 *  - ADMIN   : acesso total (inclui exclusões, gestão de áreas e bloqueio de usuários)
 *  - USUARIO : consulta, cadastra e atualiza projetos, propostas, mensagens e pagamentos
 *
 * 401 Unauthorized -> sem credenciais ou credenciais inválidas
 * 403 Forbidden    -> autenticado, mas sem o perfil necessário
 */
@Configuration
@EnableWebSecurity
public class SegurancaConfiguracao {

    @Bean
    public SecurityFilterChain filtroSeguranca(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(autorizacao -> autorizacao
                // Exclusões: somente ADMIN
                .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                // Gestão das áreas de atuação: somente ADMIN
                .requestMatchers(HttpMethod.POST, "/area/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/area/**").hasRole("ADMIN")
                // Bloqueio/desbloqueio de usuários (moderação): somente ADMIN
                .requestMatchers(HttpMethod.PATCH, "/usuario/*/bloquear", "/usuario/*/desbloquear").hasRole("ADMIN")
                // Página de erro padrão liberada para que o status original seja devolvido
                .requestMatchers("/error").permitAll()
                // Demais endpoints: qualquer usuário autenticado (ADMIN ou USUARIO)
                .anyRequest().hasAnyRole("ADMIN", "USUARIO"))
            .httpBasic(basic -> basic.authenticationEntryPoint(naoAutenticado()))
            .exceptionHandling(excecao -> excecao
                .authenticationEntryPoint(naoAutenticado())
                .accessDeniedHandler(acessoNegado()));
        return http.build();
    }

    @Bean
    public UserDetailsService usuarios(PasswordEncoder codificador) {
        // Usuários de TESTE criados exclusivamente para a atividade
        UserDetails admin = User.withUsername("admin")
                .password(codificador.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails usuario = User.withUsername("usuario")
                .password(codificador.encode("usuario123"))
                .roles("USUARIO")
                .build();

        return new InMemoryUserDetailsManager(admin, usuario);
    }

    @Bean
    public PasswordEncoder codificadorSenha() {
        return new BCryptPasswordEncoder();
    }

    // Resposta JSON para 401 Unauthorized
    private AuthenticationEntryPoint naoAutenticado() {
        return (requisicao, resposta, excecao) -> {
            resposta.setHeader("WWW-Authenticate", "Basic realm=\"LinkWork\"");
            escreverErro(resposta, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                    "Autenticação necessária: informe usuário e senha válidos (HTTP Basic)",
                    requisicao.getRequestURI());
        };
    }

    // Resposta JSON para 403 Forbidden
    private AccessDeniedHandler acessoNegado() {
        return (requisicao, resposta, excecao) -> escreverErro(resposta, HttpServletResponse.SC_FORBIDDEN, "Forbidden",
                "Acesso negado: seu perfil não tem permissão para executar esta operação",
                requisicao.getRequestURI());
    }

    private static void escreverErro(HttpServletResponse resposta, int status, String erro, String mensagem, String caminho)
            throws java.io.IOException {
        resposta.setStatus(status);
        resposta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resposta.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String json = String.format(
                "{\"dataHora\":\"%s\",\"status\":%d,\"erro\":\"%s\",\"mensagem\":\"%s\",\"caminho\":\"%s\"}",
                LocalDateTime.now(), status, erro, mensagem, caminho);
        resposta.getWriter().write(json);
    }
}
