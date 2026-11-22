package br.edu.unifio.linkwork.recursos;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.unifio.linkwork.entidades.Usuario;
import br.edu.unifio.linkwork.enums.TipoUsuario;
import br.edu.unifio.linkwork.servicos.UsuarioServico;

@RestController
@RequestMapping ("usuario")
public class UsuarioRecurso {
    @Autowired
    private UsuarioServico usuarioServico;

    // GET /usuario  ou  GET /usuario?tipo=FREELANCER
    @GetMapping
    public List<Usuario> listar(@RequestParam (required = false) TipoUsuario tipo) {
        return tipo == null ? usuarioServico.listar() : usuarioServico.listarPorTipo(tipo);
    }

    @GetMapping ("{id}")
    public Usuario buscarPorId(@PathVariable Integer id) {
        return usuarioServico.buscarPorId(id);
    }

    // GET /usuario/freelancers?competencia=Java
    @GetMapping ("freelancers")
    public List<Usuario> buscarFreelancersPorCompetencia(@RequestParam String competencia) {
        return usuarioServico.buscarFreelancersPorCompetencia(competencia);
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario usuario) {
        Usuario salvo = usuarioServico.cadastrar(usuario);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @PutMapping ("{id}")
    public Usuario atualizar(@PathVariable Integer id, @Valid @RequestBody Usuario usuario) {
        return usuarioServico.atualizar(id, usuario);
    }

    @DeleteMapping ("{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        usuarioServico.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // Restrito ao perfil ADMIN (ver SegurancaConfiguracao)
    @PatchMapping ("{id}/bloquear")
    public Usuario bloquear(@PathVariable Integer id) {
        return usuarioServico.alterarBloqueio(id, true);
    }

    // Restrito ao perfil ADMIN (ver SegurancaConfiguracao)
    @PatchMapping ("{id}/desbloquear")
    public Usuario desbloquear(@PathVariable Integer id) {
        return usuarioServico.alterarBloqueio(id, false);
    }
}
